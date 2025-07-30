package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindRestaurantByIdUseCaseImpTest {

    @InjectMocks
    private FindRestaurantByIdUseCaseImp findRestaurantByIdUseCaseImp;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurant = TestDataFactory.createRestaurant("Restaurante Teste", 1);
    }

    @Test
    void execute_shouldReturnRestaurantResponse_whenRestaurantFoundAndAuthorized() {
        when(restaurantGateway.findRestaurantById(1)).thenReturn(restaurant);
        doNothing().when(userGateway).validateAdmin();

        Restaurant response = findRestaurantByIdUseCaseImp.execute(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Restaurante Teste", response.getName());
        verify(restaurantGateway).findRestaurantById(1);
        verify(userGateway).validateAdmin();
    }

    @Test
    void execute_shouldThrowException_whenNotAuthorized() {
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateAdmin();

        BusinessException exception = assertThrows(BusinessException.class, () ->
                findRestaurantByIdUseCaseImp.execute(1)
        );
        assertEquals("Não autorizado", exception.getMessage());
        verify(userGateway).validateAdmin();
        verify(restaurantGateway, never()).findRestaurantById(anyInt());
    }
}