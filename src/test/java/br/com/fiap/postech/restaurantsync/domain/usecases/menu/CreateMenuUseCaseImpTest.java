package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateMenuUseCaseImpTest {

    @InjectMocks
    private CreateMenuUseCaseImp createMenuUseCaseImp;

    @Mock
    private MenuGateway menuGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        menuGateway = mock(MenuGateway.class);
        restaurantGateway = mock(RestaurantGateway.class);
        userGateway = mock(UserGateway.class);
        createMenuUseCaseImp = new CreateMenuUseCaseImp(menuGateway, restaurantGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        MenuRequest request = TestDataFactory.createMenuRequest();
        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);

        Menu dummyMenu = new Menu(request);
        dummyMenu.setId(100);
        Restaurant dummyRestaurant = new Restaurant(TestDataFactory.createRestaurantRequest());
        when(restaurantGateway.findRestaurantById(request.restaurantId())).thenReturn(dummyRestaurant);
        when(menuGateway.saveRestaurant(any(Menu.class))).thenReturn(dummyMenu);

        // Act
        MenuResponse response = createMenuUseCaseImp.execute(request);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).findRestaurantById(request.restaurantId());
        verify(menuGateway, times(1)).saveRestaurant(menuCaptor.capture());
        Menu capturedMenu = menuCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(dummyMenu.getId(), response.id());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        MenuRequest request = TestDataFactory.createMenuRequest();
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createMenuUseCaseImp.execute(request);
        });
        verify(restaurantGateway, never()).findRestaurantById(any());
        verify(menuGateway, never()).saveRestaurant(any());
    }

    @Test
    void testExecute_MenuGatewayThrowsException() {
        // Arrange
        MenuRequest request = TestDataFactory.createMenuRequest();
        Restaurant dummyRestaurant = new Restaurant(TestDataFactory.createRestaurantRequest());
        when(restaurantGateway.findRestaurantById(request.restaurantId())).thenReturn(dummyRestaurant);
        doNothing().when(userGateway).validateAdmin();
        when(menuGateway.saveRestaurant(any(Menu.class)))
                .thenThrow(new RuntimeException("Erro ao salvar menu"));

        // Assert
        Assertions.assertThrows(RuntimeException.class, () -> {
            createMenuUseCaseImp.execute(request);
        });
        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).findRestaurantById(request.restaurantId());
        verify(menuGateway, times(1)).saveRestaurant(any());
    }
}