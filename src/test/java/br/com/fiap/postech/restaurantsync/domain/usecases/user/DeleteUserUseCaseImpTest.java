package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteUserUseCaseImpTest {

    @InjectMocks
    private DeleteUserUseCaseImp deleteUserUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldDeleteUserSuccessfully() {
        Integer userId = 1;
        doNothing().when(userGateway).validateAdmin();
        when(userGateway.findUserOrThrow(userId)).thenReturn(null);
        doNothing().when(userGateway).deleteUserById(userId);

        deleteUserUseCaseImp.execute(userId);

        verify(userGateway).validateAdmin();
        verify(userGateway).findUserOrThrow(userId);
        verify(userGateway).deleteUserById(userId);
    }

    @Test
    void execute_shouldThrowBusinessException_whenDataIntegrityViolationOccurs() {
        Integer userId = 1;

        doNothing().when(userGateway).validateAdmin();
        when(userGateway.findUserOrThrow(userId)).thenReturn(null);
        doThrow(new DataIntegrityViolationException("")).when(userGateway).deleteUserById(userId);

        assertThrows(BusinessException.class, () ->
                deleteUserUseCaseImp.execute(userId)
        );

        verify(userGateway).validateAdmin();
        verify(userGateway).findUserOrThrow(userId);
        verify(userGateway).deleteUserById(userId);
    }
}