package br.com.fiap.postech.restaurantsync.services;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.entities.Role;
import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.entities.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.repositories.RoleRepository;
import br.com.fiap.postech.restaurantsync.repositories.UserRepository;
import br.com.fiap.postech.restaurantsync.resources.exceptions.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final Integer USER_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaim("username")).thenReturn("testuser@example.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createUserShouldReturnUserResponseWhenSuccess() {
        UserRequest userRequest = TestDataFactory.createUserRequest();

        Role role = new Role(2, "ROLE_CLIENT");

        User savedUser = new User(userRequest);
        savedUser.setId(10);

        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals(savedUser.getId(), response.id());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findUserByIdShouldReturnUserResponseWhenUserExists() {
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.findUserById(USER_ID);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void updateUserShouldReturnUserResponseWhenSuccess() {
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);

        UserRequest userRequest = TestDataFactory.createUserRequest();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.updateUser(USER_ID, userRequest);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserShouldCallRepositoryDeleteById() {
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);
        user.addRole(new Role(1, "ROLE_ADMIN"));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(anyInt());

        userService.deleteUser(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetailsProjection projection = TestDataFactory.createUserDetailsProjection();
        when(userRepository.searchUserAndRolesByEmail(anyString())).thenReturn(List.of(projection));

        UserDetails userDetails = userService.loadUserByUsername("user@test.com");

        assertNotNull(userDetails);
        assertEquals("user@test.com", userDetails.getUsername());
    }

    @Test
    void findAllPagedUsersShouldReturnPageOfUserResponse() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);
        user.addRole(new Role(1, "ROLE_ADMIN"));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Page<User> userPage = new org.springframework.data.domain.PageImpl<>(List.of(user));

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        Page<UserResponse> response = userService.findAllPagedUsers(pageRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void updatePasswordShouldUpdatePasswordWhenUserExists() {
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updatePassword(USER_ID, "newPassword"));

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void getRoleForEmailShouldReturnAdminRoleForAdminEmail() {
        Role adminRole = new Role(USER_ID, "ROLE_ADMIN");
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        Role result = TestDataFactory.invokeGetRoleForEmail(userService, "admin@restaurantsync.com");

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getAuthority());
    }

    @Test
    void authenticatedShouldThrowExceptionWhenNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            TestDataFactory.invokeAuthenticated(userService);
        });

        assertEquals("Invalid user", ex.getMessage());
    }


    @Test
    void findUserByIdShouldThrowBusinessExceptionWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.findUserById(1));

        assertEquals("Id não encontrado: 1", ex.getMessage());
    }

    @Test
    void deleteUserShouldThrowExceptionWhenUserNotFound() {
        User admin = TestDataFactory.createUser();
        admin.setId(100);
        admin.addRole(new Role(1, "ROLE_ADMIN"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(USER_ID);
        });

        assertEquals("Id não encontrado: 1", ex.getMessage());
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.updatePassword(userId, "newPassword");
        });

        assertEquals("Id não encontrado: " + userId, ex.getMessage());
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenAccessDenied() {
        User user = TestDataFactory.createUser();
        user.setId(USER_ID);

        User anotherUser = new User();
        anotherUser.setId(2);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserService spyService = Mockito.spy(userService);
        doReturn(anotherUser).when(spyService).authenticated();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.updatePassword(user.getId(), "newPassword");
        });

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void getRoleForEmailShouldThrowExceptionWhenAdminRoleNotFound() {
        when(roleRepository.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            TestDataFactory.invokeGetRoleForEmail(userService, "admin@restaurantsync.com");
        });

        assertEquals("Role ADMIN não encontrada.", ex.getMessage());
    }

    @Test
    void getRoleForEmailShouldThrowExceptionWhenClientRoleNotFound() {
        when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            TestDataFactory.invokeGetRoleForEmail(userService, "user@other.com");
        });

        assertEquals("Role CLIENT não encontrada.", ex.getMessage());
    }

    @Test
    void validateSelfOrAdminShouldThrowExceptionWhenNotAdminAndNotSelf() throws Exception {
        User me = new User();
        me.setId(2);
        me.addRole(new Role(USER_ID, "ROLE_CLIENT"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.validateSelfOrAdmin(USER_ID);
        });

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void validateSelfOrAdminShouldNotThrowWhenAdmin() throws Exception {
        User me = new User();
        me.setId(2);
        me.addRole(new Role(USER_ID, "ROLE_ADMIN"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(USER_ID));
    }

    @Test
    void validateSelfOrAdminShouldThrowWhenUserIsNotAdminAndNotSelf() throws Exception {
        User me = new User();
        me.setId(2);
        me.addRole(new Role(USER_ID, "ROLE_CLIENT"));
        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.validateSelfOrAdmin(USER_ID);
        });

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void validateSelfOrAdminShouldPassForAdminUser() throws Exception {
        User me = new User();
        me.setId(2);
        me.addRole(new Role(USER_ID, "ROLE_ADMIN"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(USER_ID));
    }

    @Test
    void validateSelfOrAdminShouldNotThrowWhenSelf() throws Exception {
        User me = new User();
        me.setId(USER_ID);
        me.addRole(new Role(2, "ROLE_CLIENT"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(USER_ID));
    }

    @Test
    void deleteUserShouldThrowExceptionWhenDataIntegrityViolation() {
        User admin = TestDataFactory.createUser();
        admin.setId(100);
        admin.addRole(new Role(1, "ROLE_ADMIN"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(admin));
        doThrow(new DataIntegrityViolationException("")).when(userRepository).deleteById(USER_ID);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(USER_ID);
        });

        assertEquals("Integrity violaton.", ex.getMessage());
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void updateUserShouldThrowExceptionWhenEntityNotFound() {
        UserRequest userRequest = TestDataFactory.createUserRequest();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.updateUser(USER_ID, userRequest);
        });

        assertEquals("Id não encontrado: " + USER_ID, ex.getMessage());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenNoUserFound() {
        String username = "nonexistentuser@example.com";

        when(userRepository.searchUserAndRolesByEmail(username)).thenReturn(null);

        UsernameNotFoundException exNull = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
        assertEquals("User not found", exNull.getMessage());

        when(userRepository.searchUserAndRolesByEmail(username)).thenReturn(List.of());

        UsernameNotFoundException exEmpty = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
        assertEquals("User not found", exEmpty.getMessage());

        verify(userRepository, times(2)).searchUserAndRolesByEmail(username);
    }
}
