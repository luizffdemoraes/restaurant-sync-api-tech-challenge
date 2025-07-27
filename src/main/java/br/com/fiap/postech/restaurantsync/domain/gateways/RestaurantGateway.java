package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;

public interface RestaurantGateway {
    Restaurant saveRestaurant(Restaurant restaurant);
    void deleterRestaurantById(Integer id);
}
