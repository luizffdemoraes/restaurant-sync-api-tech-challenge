package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class DeleteMenuUseCaseImpTest {

    @InjectMocks
    private DeleteMenuUseCaseImp deleteMenuUseCaseImp;

    @Mock
    private MenuGateway menuGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        menuGateway = mock(MenuGateway.class);
        userGateway = mock(UserGateway.class);
        deleteMenuUseCaseImp = new DeleteMenuUseCaseImp(menuGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Integer menuId = 100;
        doNothing().when(userGateway).validateAdmin();
        doNothing().when(menuGateway).deleteMenuById(menuId);

        // Act
        deleteMenuUseCaseImp.execute(menuId);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).deleteMenuById(menuId);
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        Integer menuId = 100;
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            deleteMenuUseCaseImp.execute(menuId);
        });
        verify(menuGateway, never()).deleteMenuById(anyInt());
    }

    @Test
    void testExecute_DataIntegrityException() {
        // Arrange
        Integer menuId = 100;
        doNothing().when(userGateway).validateAdmin();
        doThrow(new DataIntegrityViolationException("Violação de integridade"))
                .when(menuGateway).deleteMenuById(menuId);

        // Assert
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            deleteMenuUseCaseImp.execute(menuId);
        });
        Assertions.assertEquals("Integrity violaton.", exception.getMessage());
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).deleteMenuById(menuId);
    }

    @Test
    void execute_shouldThrowBusinessException_whenDataIntegrityViolationOccurs() {
        Integer menuId = 1;

        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.findMenuOrThrow(menuId)).thenReturn(null);
        doThrow(new DataIntegrityViolationException("")).when(menuGateway).deleteMenuById(menuId);

        assertThrows(BusinessException.class, () ->
                deleteMenuUseCaseImp.execute(menuId)
        );

        verify(userGateway).validateAdmin();
        verify(menuGateway).findMenuOrThrow(menuId);
        verify(menuGateway).deleteMenuById(menuId);
    }
}