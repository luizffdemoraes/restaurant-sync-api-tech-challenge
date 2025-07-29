package br.com.fiap.postech.restaurantsync.application.gateways;


import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserDetailsProjection;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserGatewayImplTest {

    @InjectMocks
    private UserGatewayImpl userGateway;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserEntity mockUserEntity;
    private User mockUser;
    private UserDetailsProjection mockUserDetailsProjection;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = TestDataFactory.createUser();
        mockUserEntity = UserEntity.fromDomain(mockUser);
        mockUserDetailsProjection = TestDataFactory.createUserDetailsProjection();

        Jwt jwt = Mockito.mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn(mockUser.getEmail());

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Common mocks
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUserEntity));
    }

    @Test
    void saveUser_shouldSaveAndReturnUserWithEncodedPassword() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        User result = userGateway.saveUser(mockUser);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getName(), result.getName());
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockUser.getLogin(), result.getLogin());
        assertNotNull(result.getAddress());
        verify(passwordEncoder).encode(mockUser.getPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void existsUserByEmail_shouldReturnTrueWhenUserExists() {
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(true);

        boolean result = userGateway.existsUserByEmail(mockUser.getEmail());

        assertTrue(result);
        verify(userRepository).existsByEmail(mockUser.getEmail());
    }

    @Test
    void findAllPagedUsers_shouldReturnPageOfUsers() {
        mockUserEntity.getRoleEntities().clear();
        mockUserEntity.addRole(new RoleEntity(1, "ROLE_ADMIN"));

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserEntity> mockPage = new PageImpl<>(Collections.singletonList(mockUserEntity));

        when(userRepository.findAll(pageRequest)).thenReturn(mockPage);

        Page<User> result = userGateway.findAllPagedUsers(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        User user = result.getContent().get(0);
        assertEquals(mockUser.getId(), user.getId());
        assertEquals(mockUser.getName(), user.getName());
        verify(userRepository).findAll(pageRequest);
    }

    @Test
    void findUserById_shouldReturnUserWithAddress() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUserEntity));

        User result = userGateway.findUserById(mockUser.getId());

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertNotNull(result.getAddress());
        assertEquals(mockUser.getAddress().getCity(), result.getAddress().getCity());
        verify(userRepository).findById(mockUser.getId());
    }

    @Test
    void deleteUserById_shouldDeleteUser() {
        mockUserEntity.getRoleEntities().clear();
        mockUserEntity.addRole(new RoleEntity(1, "ROLE_ADMIN"));

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUserEntity));
        doNothing().when(userRepository).deleteById(mockUser.getId());

        userGateway.deleteUserById(mockUser.getId());

        verify(userRepository).deleteById(mockUser.getId());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUserWithAddress() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

        User result = userGateway.updateUser(mockUser.getId(), mockUser);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertNotNull(result.getAddress());
        assertEquals(mockUser.getAddress().getZipCode(), result.getAddress().getZipCode());
        verify(passwordEncoder).encode(mockUser.getPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWithRoles() {
        when(userRepository.searchUserAndRolesByEmail(mockUser.getEmail()))
                .thenReturn(Collections.singletonList(mockUserDetailsProjection));

        UserDetails result = userGateway.loadUserByUsername(mockUser.getEmail());

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void authenticated_shouldReturnCurrentUserWithRoles() {
        User result = userGateway.authenticated();

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertFalse(result.getRoles().isEmpty());
    }

    @Test
    void findUserOrThrow_shouldReturnUserWithCompleteData() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUserEntity));

        User result = userGateway.findUserOrThrow(mockUser.getId());

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getName(), result.getName());
        assertNotNull(result.getAddress());
        assertFalse(result.getRoles().isEmpty());
    }

    // Additional edge case tests
    @Test
    void findUserById_shouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            userGateway.findUserById(mockUser.getId());
        });
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.searchUserAndRolesByEmail(mockUser.getEmail()))
                .thenReturn(Collections.emptyList());

        assertThrows(UsernameNotFoundException.class, () -> {
            userGateway.loadUserByUsername(mockUser.getEmail());
        });
    }

    @Test
    void authenticated_shouldThrowExceptionWhenInvalidUser() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userGateway.authenticated();
        });
    }

    // Testa updateUserPassword: senha é atualizada e salva
    @Test
    void updateUserPassword_shouldUpdatePassword() {
        String novaSenha = "novaSenha";
        when(userRepository.findById(mockUser.getId()))
                .thenReturn(Optional.of(mockUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);
        when(passwordEncoder.encode(novaSenha)).thenReturn("encodedNovaSenha");

        userGateway.updateUserPassword(mockUser.getId(), novaSenha);

        verify(userRepository).save(argThat(entity ->
                "encodedNovaSenha".equals(entity.getPassword())
        ));
        verify(passwordEncoder).encode(novaSenha);
    }

    // Testa updateUserPassword: lança exceção se usuário não encontrado
    @Test
    void updateUserPassword_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(123)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () ->
                userGateway.updateUserPassword(123, "qualquer")
        );
    }

    // Testa validateAdmin: sem ROLE_ADMIN -> exceção
    @Test
    void validateAdmin_shouldThrowExceptionWhenUserIsNotAdmin() {
        // Simula usuário sem ROLE_ADMIN
        when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUserEntity));

        // Remove todas roles
        mockUserEntity.getRoleEntities().clear();
        // Adiciona só CLIENT
        mockUserEntity.addRole(new RoleEntity(2, "ROLE_CLIENT"));

        assertThrows(BusinessException.class, () -> {
            userGateway.validateAdmin();
        });
    }

    // Testa validateAdmin: com admin - não lança exceção
    @Test
    void validateAdmin_shouldPassWhenUserIsAdmin() {
        // Simula ROLE_ADMIN
        mockUserEntity.getRoleEntities().clear();
        mockUserEntity.addRole(new RoleEntity(1, "ROLE_ADMIN"));
        when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUserEntity));

        assertDoesNotThrow(() -> userGateway.validateAdmin());
    }

    // Testa validateSelfOrAdmin: usuário normal tentando acessar outro id
    @Test
    void validateSelfOrAdmin_shouldThrowExceptionWhenUserIsNotSelfOrAdmin() {
        mockUserEntity.getRoleEntities().clear(); // Não é admin
        mockUserEntity.addRole(new RoleEntity(2, "ROLE_CLIENT"));
        when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUserEntity));

        // Tenta validar acesso com outro ID (não é self)
        assertThrows(BusinessException.class, () ->
                userGateway.validateSelfOrAdmin(999)
        );
    }

    // Testa validateSelfOrAdmin: usuário é ele mesmo
    @Test
    void validateSelfOrAdmin_shouldPassWhenUserIsSelf() {
        mockUserEntity.getRoleEntities().clear(); // Não é admin
        mockUserEntity.addRole(new RoleEntity(2, "ROLE_CLIENT"));
        when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUserEntity));

        assertDoesNotThrow(() -> userGateway.validateSelfOrAdmin(mockUser.getId()));
    }

    @Test
    void validateSelfOrAdmin_shouldPassWhenUserIsAdmin() {
        mockUserEntity.getRoleEntities().clear();
        mockUserEntity.addRole(new RoleEntity(1, "ROLE_ADMIN"));
        when(userRepository.findByEmail(mockUser.getEmail()))
                .thenReturn(Optional.of(mockUserEntity));

        assertDoesNotThrow(() -> userGateway.validateSelfOrAdmin(999));
    }

    @Test
    void validateUserByOwnerId_shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                userGateway.validateUserByOwnerId(999)
        );
    }


    @Test
    void validateUserByOwnerId_shouldPass_whenOwnerIsAdmin() {
        // Arrange
        Integer ownerId = 10;
        User adminUser = TestDataFactory.createUser();
        adminUser.addRole(new Role(1, "ROLE_ADMIN"));

        // Mock the internal validateAdmin() call to do nothing
        UserGatewayImpl spyGateway = spy(userGateway);
        doNothing().when(spyGateway).validateAdmin();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(UserEntity.fromDomain(adminUser)));

        // Act & Assert
        assertDoesNotThrow(() -> spyGateway.validateUserByOwnerId(ownerId));
        verify(spyGateway).validateAdmin();
        verify(userRepository).findById(ownerId);
    }

    @Test
    void validateUserByOwnerId_shouldThrowBusinessException_whenOwnerIsNotAdmin() {
        // Arrange
        Integer ownerId = 20;
        User commonUser = TestDataFactory.createUser(); // Has only ROLE_CLIENT by default

        // Mock the internal validateAdmin() call to do nothing
        UserGatewayImpl spyGateway = spy(userGateway);
        doNothing().when(spyGateway).validateAdmin();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(UserEntity.fromDomain(commonUser)));

        // Act & Assert
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> spyGateway.validateUserByOwnerId(ownerId)
        );
        assertEquals("Restaurant owner must be an ADMIN user", ex.getMessage());
        verify(spyGateway).validateAdmin();
        verify(userRepository).findById(ownerId);
    }

    @Test
    void validateUserByOwnerId_shouldThrow_whenOwnerNotFound() {
        // Arrange
        Integer ownerId = 999;

        // Mock the internal validateAdmin() call to do nothing
        UserGatewayImpl spyGateway = spy(userGateway);
        doNothing().when(spyGateway).validateAdmin();

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> spyGateway.validateUserByOwnerId(ownerId));
        verify(spyGateway).validateAdmin();
        verify(userRepository).findById(ownerId);
    }
}


