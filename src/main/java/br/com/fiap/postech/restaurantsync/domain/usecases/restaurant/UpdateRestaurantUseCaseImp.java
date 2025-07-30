package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class UpdateRestaurantUseCaseImp  implements UpdateRestaurantUseCase{

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public UpdateRestaurantUseCaseImp(RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Restaurant execute(Integer id, Restaurant restaurant) {
        this.userGateway.validateAdmin();
        if (restaurant.getOwnerId() != null) {
            userGateway.validateUserByOwnerId(restaurant.getOwnerId());
        }
        return this.restaurantGateway.updateRestaurant(id, restaurant);
    }
}
