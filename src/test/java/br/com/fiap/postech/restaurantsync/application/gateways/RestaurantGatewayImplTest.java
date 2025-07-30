package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.RestaurantMapper;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RestaurantRepository;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantGatewayImplTest {

    @InjectMocks
    private RestaurantGatewayImpl restaurantGateway;

    @Mock
    private RestaurantRepository restaurantRepository;

    private RestaurantEntity mockRestaurantEntity;
    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockRestaurant = TestDataFactory.createRestaurant("Restaurante Teste", 1);
        mockRestaurantEntity = RestaurantMapper.toEntity(mockRestaurant);
    }

    @Test
    void saveRestaurant_shouldSaveAndReturnRestaurant() {
        when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(mockRestaurantEntity);

        Restaurant result = restaurantGateway.saveRestaurant(mockRestaurant);

        assertNotNull(result);
        assertEquals(mockRestaurant.getId(), result.getId());
        assertEquals(mockRestaurant.getName(), result.getName());
        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }

    @Test
    void findAllPagedRestaurants_shouldReturnPageOfRestaurants() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<RestaurantEntity> mockPage = new PageImpl<>(Collections.singletonList(mockRestaurantEntity));

        when(restaurantRepository.findAll(pageRequest)).thenReturn(mockPage);

        Page<Restaurant> result = restaurantGateway.findAllPagedRestaurants(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        Restaurant restaurant = result.getContent().get(0);
        assertEquals(mockRestaurant.getId(), restaurant.getId());
        assertEquals(mockRestaurant.getName(), restaurant.getName());
        verify(restaurantRepository).findAll(pageRequest);
    }

    @Test
    void findRestaurantById_shouldReturnRestaurant() {
        when(restaurantRepository.findById(mockRestaurant.getId())).thenReturn(Optional.of(mockRestaurantEntity));

        Restaurant result = restaurantGateway.findRestaurantById(mockRestaurant.getId());

        assertNotNull(result);
        assertEquals(mockRestaurant.getId(), result.getId());
        assertEquals(mockRestaurant.getName(), result.getName());
        verify(restaurantRepository).findById(mockRestaurant.getId());
    }

    @Test
    void findRestaurantById_shouldThrowExceptionWhenNotFound() {
        when(restaurantRepository.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                restaurantGateway.findRestaurantById(999)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("id not found"));
        verify(restaurantRepository).findById(999);
    }

    @Test
    void deleterRestaurantById_shouldDeleteRestaurant() {
        when(restaurantRepository.findById(mockRestaurant.getId())).thenReturn(Optional.of(mockRestaurantEntity));
        doNothing().when(restaurantRepository).deleteById(mockRestaurant.getId());

        assertDoesNotThrow(() -> restaurantGateway.deleterRestaurantById(mockRestaurant.getId()));

        verify(restaurantRepository).deleteById(mockRestaurant.getId());
    }

    @Test
    void deleterRestaurantById_shouldThrowExceptionWhenNotFound() {
        doThrow(new BusinessException("Id not found: 999")).when(restaurantRepository).deleteById(999);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                restaurantGateway.deleterRestaurantById(999)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("id not found"));
        verify(restaurantRepository).deleteById(999);
    }


    @Test
    void updateRestaurant_shouldUpdateAndReturnRestaurant() {
        when(restaurantRepository.findById(mockRestaurant.getId())).thenReturn(Optional.of(mockRestaurantEntity));
        when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(mockRestaurantEntity);

        Restaurant updated = restaurantGateway.updateRestaurant(mockRestaurant.getId(), mockRestaurant);

        assertNotNull(updated);
        assertEquals(mockRestaurant.getId(), updated.getId());
        assertEquals(mockRestaurant.getName(), updated.getName());
        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }

    @Test
    void saveRestaurant_shouldThrowDataIntegrityViolationException() {
        when(restaurantRepository.save(any(RestaurantEntity.class)))
                .thenThrow(new DataIntegrityViolationException("erro"));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () ->
                restaurantGateway.saveRestaurant(mockRestaurant)
        );
        assertTrue(exception.getMessage().toLowerCase().contains("erro"));
        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }
}