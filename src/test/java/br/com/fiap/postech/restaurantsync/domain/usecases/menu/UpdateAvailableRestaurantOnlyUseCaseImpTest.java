package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class UpdateAvailableRestaurantOnlyUseCaseImpTest {

    @InjectMocks
    private UpdateAvailableRestaurantOnlyUseCaseImp updateAvailableRestaurantOnlyUseCaseImp;

    @Mock
    private MenuGateway menuGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        menuGateway = mock(MenuGateway.class);
        userGateway = mock(UserGateway.class);
        updateAvailableRestaurantOnlyUseCaseImp = new UpdateAvailableRestaurantOnlyUseCaseImp(menuGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Integer id = 1;
        Boolean availableOnlyRestaurant = true;
        Menu dummyMenu = new Menu(TestDataFactory.createMenuRequest());
        dummyMenu.setId(id);
        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.updateAvailableOnlyRestaurant(id, availableOnlyRestaurant)).thenReturn(dummyMenu);

        // Act
        MenuResponse response = updateAvailableRestaurantOnlyUseCaseImp.execute(id, availableOnlyRestaurant);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).updateAvailableOnlyRestaurant(id, availableOnlyRestaurant);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(id, response.id());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        Integer id = 1;
        Boolean availableOnlyRestaurant = true;
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            updateAvailableRestaurantOnlyUseCaseImp.execute(id, availableOnlyRestaurant);
        });
        verify(menuGateway, never()).updateAvailableOnlyRestaurant(any(), any());
    }

    @Test
    void testExecute_UpdateThrowsException() {
        // Arrange
        Integer id = 1;
        Boolean availableOnlyRestaurant = true;
        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.updateAvailableOnlyRestaurant(id, availableOnlyRestaurant))
                .thenThrow(new RuntimeException("Erro ao atualizar menu"));

        // Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            updateAvailableRestaurantOnlyUseCaseImp.execute(id, availableOnlyRestaurant);
        });
        Assertions.assertEquals("Erro ao atualizar menu", exception.getMessage());
        verify(userGateway, times(1)).validateAdmin();
        verify(menuGateway, times(1)).updateAvailableOnlyRestaurant(id, availableOnlyRestaurant);
    }
}