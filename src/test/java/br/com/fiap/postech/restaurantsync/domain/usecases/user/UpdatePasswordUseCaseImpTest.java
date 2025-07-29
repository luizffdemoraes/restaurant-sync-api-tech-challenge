package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdatePasswordUseCaseImpTest {

    @InjectMocks
    private UpdatePasswordUseCaseImp updatePasswordUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldUpdatePassword_whenUserIsAuthorized() {
        Integer userId = 1;
        String newPassword = "novaSenha123";

        doNothing().when(userGateway).validateSelfOrAdmin(userId);
        doNothing().when(userGateway).updateUserPassword(userId, newPassword);

        updatePasswordUseCaseImp.execute(userId, newPassword);

        verify(userGateway).validateSelfOrAdmin(userId);
        verify(userGateway).updateUserPassword(userId, newPassword);
    }

    @Test
    void execute_shouldThrowBusinessException_whenUserNotAuthorized() {
        Integer userId = 1;
        String newPassword = "novaSenha123";

        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelfOrAdmin(userId);

        assertThrows(BusinessException.class, () ->
                updatePasswordUseCaseImp.execute(userId, newPassword)
        );

        verify(userGateway).validateSelfOrAdmin(userId);
        verify(userGateway, never()).updateUserPassword(anyInt(), anyString());
    }
}