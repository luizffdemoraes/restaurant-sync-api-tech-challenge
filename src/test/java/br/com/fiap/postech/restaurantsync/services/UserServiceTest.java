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

        Role role = new Role(2L, "ROLE_CLIENT");

        User savedUser = new User(userRequest);
        savedUser.setId(10L);

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
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.findUserById(1L);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUserShouldReturnUserResponseWhenSuccess() {
        User user = TestDataFactory.createUser();
        user.setId(1L);

        UserRequest userRequest = TestDataFactory.createUserRequest();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.updateUser(1L, userRequest);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserShouldCallRepositoryDeleteById() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
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
        user.setId(1L);

        Page<User> userPage = new org.springframework.data.domain.PageImpl<>(List.of(user));

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        Page<UserResponse> response = userService.findAllPagedUsers(pageRequest);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void updatePasswordShouldUpdatePasswordWhenUserExists() {
        User user = TestDataFactory.createUser();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updatePassword(1L, "newPassword"));

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void getRoleForEmailShouldReturnAdminRoleForAdminEmail() {
        Role adminRole = new Role(1L, "ROLE_ADMIN");
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
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.findUserById(1L));

        assertEquals("Entity not Found", ex.getMessage());
    }

    @Test
    void deleteUserShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;

        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(userId);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("Id not found.", ex.getMessage());

        verify(userRepository).deleteById(userId);
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.updatePassword(userId, "newPassword");
        });

        assertEquals("Id not found: " + userId, ex.getMessage());
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenAccessDenied() {
        User user = TestDataFactory.createUser();
        user.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

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
        me.setId(2L);
        me.addRole(new Role(1L, "ROLE_CLIENT"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.validateSelfOrAdmin(1L);
        });

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void validateSelfOrAdminShouldNotThrowWhenAdmin() throws Exception {
        User me = new User();
        me.setId(2L);
        me.addRole(new Role(1L, "ROLE_ADMIN"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(1L));
    }

    @Test
    void validateSelfOrAdminShouldThrowWhenUserIsNotAdminAndNotSelf() throws Exception {
        User me = new User();
        me.setId(2L);
        me.addRole(new Role(1L, "ROLE_CLIENT"));
        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            spyService.validateSelfOrAdmin(1L);
        });

        assertEquals("Access denied", ex.getMessage());
    }

    @Test
    void validateSelfOrAdminShouldPassForAdminUser() throws Exception {
        User me = new User();
        me.setId(2L);
        me.addRole(new Role(1L, "ROLE_ADMIN"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(1L));
    }

    @Test
    void validateSelfOrAdminShouldNotThrowWhenSelf() throws Exception {
        User me = new User();
        me.setId(1L);
        me.addRole(new Role(2L, "ROLE_CLIENT"));

        UserService spyService = Mockito.spy(userService);
        doReturn(me).when(spyService).authenticated();

        assertDoesNotThrow(() -> spyService.validateSelfOrAdmin(1L));
    }

    @Test
    void deleteUserShouldThrowExceptionWhenDataIntegrityViolation() {
        Long userId = 1L;

        doThrow(new DataIntegrityViolationException("")).when(userRepository).deleteById(userId);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("Integrity violaton.", ex.getMessage());

        verify(userRepository).deleteById(userId);
    }

    @Test
    void updateUserShouldThrowExceptionWhenEntityNotFound() {
        Long userId = 1L;
        UserRequest userRequest = TestDataFactory.createUserRequest();

        when(userRepository.getReferenceById(userId)).thenThrow(EntityNotFoundException.class);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.updateUser(userId, userRequest);
        });

        assertEquals("Id not found:" + userId, ex.getMessage());

        verify(userRepository).getReferenceById(userId);
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
