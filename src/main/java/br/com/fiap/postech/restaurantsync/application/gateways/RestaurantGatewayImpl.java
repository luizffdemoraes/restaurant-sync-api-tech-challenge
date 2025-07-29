package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RestaurantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class RestaurantGatewayImpl implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;

    public RestaurantGatewayImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public void deleterRestaurantById(Integer id) {
        try {
            this.restaurantRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }

    @Override
    public Restaurant findRestaurantById(Integer id) {
        return findRestaurantOrThrow(id);
    }

    @Override
    public Page<Restaurant> findAllPagedRestaurants(PageRequest pageRequest) {
        Page<RestaurantEntity> pagedRestaurants = this.restaurantRepository.findAll(pageRequest);
        return pagedRestaurants.map(RestaurantEntity::toDomain);
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity responseEntity = RestaurantEntity.fromDomain(restaurant);
        RestaurantEntity saved = this.restaurantRepository.save(responseEntity);
        return saved.toDomain();
    }

    @Override
    public Restaurant updateRestaurant(Integer id, Restaurant restaurantRequest) {
        Restaurant restaurant = findRestaurantOrThrow(id);
        restaurant.setName(restaurantRequest.getName() != null ? restaurantRequest.getName() : restaurant.getName());
        restaurant.setAddress(restaurantRequest.getAddress() != null ? restaurantRequest.getAddress() : restaurant.getAddress());
        restaurant.setCuisineType(restaurantRequest.getCuisineType() != null ? restaurantRequest.getCuisineType() : restaurant.getCuisineType());
        restaurant.setOpeningHours(restaurantRequest.getOpeningHours() != null ? restaurantRequest.getOpeningHours() : restaurant.getOpeningHours());
        restaurant.setOwnerId(restaurantRequest.getOwnerId() != null ? restaurantRequest.getOwnerId() : restaurant.getOwnerId());
        RestaurantEntity saved = this.restaurantRepository.save(RestaurantEntity.fromDomain(restaurant));
        return saved.toDomain();
    }

    private Restaurant findRestaurantOrThrow(Integer id) {
        return this.restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Id not found: " + id)).toDomain();
    }
}
