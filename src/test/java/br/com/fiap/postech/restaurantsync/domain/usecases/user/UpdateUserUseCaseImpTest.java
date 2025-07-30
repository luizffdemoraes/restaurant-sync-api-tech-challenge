package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.UserMapper;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateUserUseCaseImpTest {
    @InjectMocks
    private UpdateUserUseCaseImp updateUserUseCaseImp;

    @Mock
    private UserGateway userGateway;

    private User userRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequest = UserMapper.toDomain(TestDataFactory.createUserRequest());
    }

    @Test
    void execute_shouldUpdateUser_whenAuthorized() {
        Integer userId = 1;

        doNothing().when(userGateway).validateSelfOrAdmin(userId);
        when(userGateway.updateUser(eq(userId), any(User.class))).thenReturn(userRequest);

        User response = updateUserUseCaseImp.execute(userId, userRequest);

        assertNotNull(response);
        assertEquals(userRequest.getName(), response.getName());
        verify(userGateway).validateSelfOrAdmin(userId);
        verify(userGateway).updateUser(eq(userId), any(User.class));
    }

    @Test
    void execute_shouldThrowBusinessException_whenNotAuthorized() {
        Integer userId = 1;
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelfOrAdmin(userId);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                updateUserUseCaseImp.execute(userId, userRequest)
        );
        assertEquals("Não autorizado", exception.getMessage());

        verify(userGateway).validateSelfOrAdmin(userId);
        verify(userGateway, never()).updateUser(anyInt(), any(User.class));
    }
}