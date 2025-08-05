package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RestaurantGateway {
    void deleterRestaurantById(Integer id);
    Restaurant saveRestaurant(Restaurant restaurant);
    Restaurant findRestaurantById(Integer id);
    Page<Restaurant> findAllPagedRestaurants(PageRequest pageRequest);
    Restaurant updateRestaurant(Integer id, Restaurant restaurant);
    Restaurant findRestaurantOrThrow(Integer id);
}
