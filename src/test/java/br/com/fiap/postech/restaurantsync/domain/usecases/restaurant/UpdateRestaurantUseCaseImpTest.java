package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateRestaurantUseCaseImpTest {

    @InjectMocks
    private UpdateRestaurantUseCaseImp updateRestaurantUseCaseImp;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        restaurantGateway = mock(RestaurantGateway.class);
        userGateway = mock(UserGateway.class);
        updateRestaurantUseCaseImp = new UpdateRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Integer restaurantId = 100;
        RestaurantRequest request = TestDataFactory.createRestaurantRequest();

        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        Restaurant updatedRestaurant = new Restaurant(request);
        updatedRestaurant.setId(restaurantId);

        doNothing().when(userGateway).validateAdmin();
        doNothing().when(userGateway).validateUserByOwnerId(request.ownerId());
        when(restaurantGateway.updateRestaurant(eq(restaurantId), any(Restaurant.class)))
                .thenReturn(updatedRestaurant);

        // Act
        RestaurantResponse response = updateRestaurantUseCaseImp.execute(restaurantId, request);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(userGateway, times(1)).validateUserByOwnerId(request.ownerId());
        verify(restaurantGateway, times(1)).updateRestaurant(eq(restaurantId), restaurantCaptor.capture());

        Restaurant capturedRestaurant = restaurantCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(updatedRestaurant.getId(), response.id());
        Assertions.assertEquals(updatedRestaurant.getName(), response.name());
        Assertions.assertEquals(updatedRestaurant.getCuisineType(), response.cuisineType());
    }

    @Test
    void testExecute_NullOwnerId() {
        // Arrange
        Integer restaurantId = 101;
        RestaurantRequest request = TestDataFactory.createRestaurantRequest();
        // Supondo que seja possível clonar e customizar o request para ownerId null
        RestaurantRequest requestWithoutOwner = new RestaurantRequest(
                request.name(),
                request.address(),
                request.cuisineType(),
                request.openingHours(),
                null
        );

        Restaurant updatedRestaurant = new Restaurant(requestWithoutOwner);
        updatedRestaurant.setId(restaurantId);

        doNothing().when(userGateway).validateAdmin();
        when(restaurantGateway.updateRestaurant(eq(restaurantId), any(Restaurant.class)))
                .thenReturn(updatedRestaurant);

        // Act
        RestaurantResponse response = updateRestaurantUseCaseImp.execute(restaurantId, requestWithoutOwner);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(userGateway, never()).validateUserByOwnerId(any());
        verify(restaurantGateway, times(1)).updateRestaurant(eq(restaurantId), any(Restaurant.class));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(updatedRestaurant.getId(), response.id());
    }

    @Test
    void testExecute_InvalidAdmin() {
        // Arrange
        Integer restaurantId = 102;
        RestaurantRequest request = TestDataFactory.createRestaurantRequest();

        doThrow(new SecurityException("Não autorizado")).when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(SecurityException.class, () -> {
            updateRestaurantUseCaseImp.execute(restaurantId, request);
        });

        verify(userGateway, times(1)).validateAdmin();
        verify(userGateway, never()).validateUserByOwnerId(any());
        verify(restaurantGateway, never()).updateRestaurant(any(), any());
    }

    @Test
    void testExecute_InvalidOwner() {
        // Arrange
        Integer restaurantId = 103;
        RestaurantRequest request = TestDataFactory.createRestaurantRequest();

        doNothing().when(userGateway).validateAdmin();
        doThrow(new IllegalArgumentException("Proprietário inválido"))
                .when(userGateway).validateUserByOwnerId(request.ownerId());

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            updateRestaurantUseCaseImp.execute(restaurantId, request);
        });
        verify(userGateway, times(1)).validateAdmin();
        verify(userGateway, times(1)).validateUserByOwnerId(request.ownerId());
        verify(restaurantGateway, never()).updateRestaurant(any(), any());
    }

    @Test
    void testExecute_RestaurantGatewayThrowsException() {
        // Arrange
        Integer restaurantId = 104;
        RestaurantRequest request = TestDataFactory.createRestaurantRequest();

        doNothing().when(userGateway).validateAdmin();
        doNothing().when(userGateway).validateUserByOwnerId(request.ownerId());
        when(restaurantGateway.updateRestaurant(eq(restaurantId), any(Restaurant.class)))
                .thenThrow(new RuntimeException("Erro ao atualizar restaurante"));

        // Assert
        Assertions.assertThrows(RuntimeException.class, () -> {
            updateRestaurantUseCaseImp.execute(restaurantId, request);
        });
        verify(userGateway, times(1)).validateAdmin();
        verify(userGateway, times(1)).validateUserByOwnerId(request.ownerId());
        verify(restaurantGateway, times(1)).updateRestaurant(eq(restaurantId), any(Restaurant.class));
    }
}