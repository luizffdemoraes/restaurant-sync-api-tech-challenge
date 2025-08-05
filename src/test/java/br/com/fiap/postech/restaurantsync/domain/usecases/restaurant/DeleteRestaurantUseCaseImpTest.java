package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
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

class DeleteRestaurantUseCaseImpTest {

    @InjectMocks
    private DeleteRestaurantUseCaseImp deleteRestaurantUseCaseImp;

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        restaurantGateway = mock(RestaurantGateway.class);
        userGateway = mock(UserGateway.class);
        deleteRestaurantUseCaseImp = new DeleteRestaurantUseCaseImp(restaurantGateway, userGateway);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        doNothing().when(userGateway).validateAdmin();
        doNothing().when(restaurantGateway).deleterRestaurantById(anyInt());

        // Act
        deleteRestaurantUseCaseImp.execute(10);

        // Assert
        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).deleterRestaurantById(10);
    }

    @Test
    void testExecute_WithoutAdminPermission() {
        // Arrange
        doThrow(new SecurityException("Não autorizado")).when(userGateway).validateAdmin();

        // Assert
        Assertions.assertThrows(SecurityException.class, () -> {
            deleteRestaurantUseCaseImp.execute(5);
        });
        verify(restaurantGateway, never()).deleterRestaurantById(anyInt());
    }

    @Test
    void testExecute_DataIntegrityViolation_ThrowsBusinessException() {
        // Arrange
        doNothing().when(userGateway).validateAdmin();
        doThrow(new DataIntegrityViolationException("Foreign key")).when(restaurantGateway).deleterRestaurantById(99);

        // Assert
        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> {
            deleteRestaurantUseCaseImp.execute(99);
        });
        Assertions.assertEquals("Integrity violaton.", ex.getMessage());

        verify(userGateway, times(1)).validateAdmin();
        verify(restaurantGateway, times(1)).deleterRestaurantById(99);
    }

    @Test
    void execute_shouldThrowBusinessException_whenDataIntegrityViolationOccurs() {
        Integer restaurantId = 1;

        doNothing().when(userGateway).validateAdmin();
        when(restaurantGateway.findRestaurantOrThrow(restaurantId)).thenReturn(null);
        doThrow(new DataIntegrityViolationException("")).when(restaurantGateway).deleterRestaurantById(restaurantId);

        assertThrows(BusinessException.class, () ->
                deleteRestaurantUseCaseImp.execute(restaurantId)
        );

        verify(userGateway).validateAdmin();
        verify(restaurantGateway).findRestaurantOrThrow(restaurantId);
        verify(restaurantGateway).deleterRestaurantById(restaurantId);
    }
}