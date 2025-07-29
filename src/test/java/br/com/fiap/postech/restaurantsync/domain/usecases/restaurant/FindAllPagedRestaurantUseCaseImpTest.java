package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static br.com.fiap.postech.restaurantsync.factories.TestDataFactory.createRestaurant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllPagedRestaurantUseCaseImpTest {

    @InjectMocks
    private FindAllPagedRestaurantUseCaseImp findAllPagedRestaurantUseCaseImp;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldReturnPagedRestaurantResponses_whenAdminValidated() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Restaurant restaurant1 = createRestaurant("Restaurante 1", 1);
        Restaurant restaurant2 = createRestaurant("Restaurante 2", 2);

        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);
        Page<Restaurant> restaurantPage = new PageImpl<>(restaurants, pageRequest, restaurants.size());

        doNothing().when(userGateway).validateAdmin();
        when(restaurantGateway.findAllPagedRestaurants(pageRequest)).thenReturn(restaurantPage);

        Page<RestaurantResponse> responsePage = findAllPagedRestaurantUseCaseImp.execute(pageRequest);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1, responsePage.getContent().get(0).id());
        assertEquals(2, responsePage.getContent().get(1).id());
        verify(userGateway).validateAdmin();
        verify(restaurantGateway).findAllPagedRestaurants(pageRequest);
    }

    @Test
    void execute_shouldThrowBusinessException_whenAdminValidationFails() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateAdmin();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> findAllPagedRestaurantUseCaseImp.execute(pageRequest));
        assertEquals("Não autorizado", exception.getMessage());
        verify(userGateway).validateAdmin();
        verify(restaurantGateway, never()).findAllPagedRestaurants(pageRequest);
    }
}