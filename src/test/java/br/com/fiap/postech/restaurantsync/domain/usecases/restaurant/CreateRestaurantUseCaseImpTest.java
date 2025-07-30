package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.RestaurantMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateRestaurantUseCaseImpTest {

    @InjectMocks
    private CreateRestaurantUseCaseImp createRestaurantUseCaseImp;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        restaurantGateway = mock(RestaurantGateway.class);
        userGateway = mock(UserGateway.class);
        createRestaurantUseCaseImp = new CreateRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        RestaurantRequest request = TestDataFactory.createRestaurantRequest(); // Use o objeto de request esperado do factory

        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

        Restaurant dummyRestaurant = RestaurantMapper.toDomain(request);
        dummyRestaurant.setId(100);
        when(restaurantGateway.saveRestaurant(any(Restaurant.class))).thenReturn(dummyRestaurant);

        // Act
        Restaurant response = createRestaurantUseCaseImp.execute(dummyRestaurant);

        // Assert
        verify(userGateway, times(1)).validateUserByOwnerId(request.ownerId());
        verify(restaurantGateway, times(1)).saveRestaurant(restaurantCaptor.capture());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(dummyRestaurant.getId(), response.getId());
        Assertions.assertEquals(dummyRestaurant.getName(), response.getName());
    }

    @Test
    void testExecute_InvalidUser() {
        // Arrange
        Restaurant request = RestaurantMapper.toDomain(TestDataFactory.createRestaurantRequest());
        doThrow(new IllegalArgumentException("Usuário inválido"))
                .when(userGateway).validateUserByOwnerId(request.getOwnerId());

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createRestaurantUseCaseImp.execute(request);
        });
        verify(restaurantGateway, never()).saveRestaurant(any());
    }

    @Test
    void testExecute_RestaurantGatewayThrowsException() {
        // Arrange
        Restaurant request = RestaurantMapper.toDomain(TestDataFactory.createRestaurantRequest());

        doNothing().when(userGateway).validateUserByOwnerId(request.getOwnerId());
        when(restaurantGateway.saveRestaurant(any(Restaurant.class)))
                .thenThrow(new RuntimeException("Erro ao salvar restaurante"));

        // Assert
        Assertions.assertThrows(RuntimeException.class, () -> {
            createRestaurantUseCaseImp.execute(request);
        });
        verify(userGateway, times(1)).validateUserByOwnerId(request.getOwnerId());
        verify(restaurantGateway, times(1)).saveRestaurant(any(Restaurant.class));
    }
}
