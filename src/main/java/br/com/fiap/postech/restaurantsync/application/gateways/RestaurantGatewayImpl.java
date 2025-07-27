package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RestaurantEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RestaurantRepository;
import org.springframework.dao.DataIntegrityViolationException;

public class RestaurantGatewayImpl implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;

    public RestaurantGatewayImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity responseEntity = RestaurantEntity.fromDomain(restaurant);
        RestaurantEntity saved = this.restaurantRepository.save(responseEntity);
        return saved.toDomain();
    }

    @Override
    public void deleterRestaurantById(Integer id) {
        try {
            this.restaurantRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Integrity violaton.");
        }
    }
}
