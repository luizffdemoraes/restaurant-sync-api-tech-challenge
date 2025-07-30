package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.UserMapper;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserUseCaseImpTest {

    @InjectMocks
    private CreateUserUseCaseImp createUserUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @Mock
    private RoleGateway roleGateway;

    private User userRequestAdmin;
    private User userRequestClient;
    private Role roleAdmin;
    private Role roleClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequestAdmin = UserMapper.toDomain(new UserRequest("Admin", "admin@restaurantsync.com", "adminUser", "adminPass", TestDataFactory.createAddressRequest()));
        userRequestClient = UserMapper.toDomain(new UserRequest("Client", "client@example.com", "clientUser", "clientPass", TestDataFactory.createAddressRequest()));

        roleAdmin = new Role(1, "ROLE_ADMIN");
        roleClient = new Role(2, "ROLE_CLIENT");
    }

    @Test
    void execute_shouldCreateAdminUser_whenEmailEndsWithRestaurantsync() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));

        // Simula a gravação do usuário definindo um ID
        when(userGateway.saveUser(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        User response = createUserUseCaseImp.execute(userRequestAdmin);

        assertNotNull(response);
        assertEquals(1, response.getId());
        verify(userGateway).existsUserByEmail(userRequestAdmin.getEmail());
        verify(roleGateway).findByAuthority("ROLE_ADMIN");
        verify(userGateway).saveUser(ArgumentMatchers.any(User.class));
    }

    @Test
    void execute_shouldCreateClientUser_whenEmailDoesNotEndWithRestaurantsync() {
        when(userGateway.existsUserByEmail(userRequestClient.getEmail())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_CLIENT")).thenReturn(Optional.of(roleClient));

        when(userGateway.saveUser(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2);
            return user;
        });

        User response = createUserUseCaseImp.execute(userRequestClient);

        assertNotNull(response);
        assertEquals(2, response.getId());
        verify(userGateway).existsUserByEmail(userRequestClient.getEmail());
        verify(roleGateway).findByAuthority("ROLE_CLIENT");
        verify(userGateway).saveUser(ArgumentMatchers.any(User.class));
    }

    @Test
    void execute_shouldThrowException_whenEmailAlreadyRegistered() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> createUserUseCaseImp.execute(userRequestAdmin));
        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void execute_shouldThrowException_whenRoleNotFound() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_ADMIN")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> createUserUseCaseImp.execute(userRequestAdmin));
        assertEquals("Role not found: ROLE_ADMIN", exception.getMessage());
    }
}