package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.MenuMapper;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.RestaurantMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class UpdateMenuUseCaseImpTest {

    @InjectMocks
    private UpdateMenuUseCaseImp updateMenuUseCaseImp;

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
        updateMenuUseCaseImp = new UpdateMenuUseCaseImp(menuGateway, restaurantGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Integer id = 1;
        Menu request = MenuMapper.toDomain(TestDataFactory.createMenuRequest());
        Restaurant dummyRestaurant = RestaurantMapper.toDomain(TestDataFactory.createRestaurantRequest());
        request.setId(id);
        doNothing().when(userGateway).validateAdmin();
        when(restaurantGateway.findRestaurantById(request.getRestaurantId())).thenReturn(dummyRestaurant);
        when(menuGateway.updateMenu(eq(id), any(Menu.class))).thenReturn(request);

        // Act
        Menu response = updateMenuUseCaseImp.execute(id, request);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).findRestaurantById(request.getRestaurantId());
        verify(menuGateway, times(1)).updateMenu(eq(id), any(Menu.class));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(id, response.getId());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        Integer id = 1;
        Menu request = MenuMapper.toDomain(TestDataFactory.createMenuRequest());
        doThrow(new IllegalArgumentException("Usuário não é admin"))
                .when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            updateMenuUseCaseImp.execute(id, request);
        });
        verify(restaurantGateway, never()).findRestaurantById(any());
        verify(menuGateway, never()).updateMenu(anyInt(), any());
    }

    @Test
    void testExecute_MenuGatewayThrowsException() {
        // Arrange
        Integer id = 1;
        Menu request = MenuMapper.toDomain(TestDataFactory.createMenuRequest());
        Restaurant dummyRestaurant = RestaurantMapper.toDomain(TestDataFactory.createRestaurantRequest());
        doNothing().when(userGateway).validateAdmin();
        when(restaurantGateway.findRestaurantById(request.getRestaurantId())).thenReturn(dummyRestaurant);
        when(menuGateway.updateMenu(eq(id), any(Menu.class)))
                .thenThrow(new RuntimeException("Erro ao atualizar menu"));

        // Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            updateMenuUseCaseImp.execute(id, request);
        });
        Assertions.assertEquals("Erro ao atualizar menu", exception.getMessage());
        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).findRestaurantById(request.getRestaurantId());
        verify(menuGateway, times(1)).updateMenu(eq(id), any(Menu.class));
    }
}