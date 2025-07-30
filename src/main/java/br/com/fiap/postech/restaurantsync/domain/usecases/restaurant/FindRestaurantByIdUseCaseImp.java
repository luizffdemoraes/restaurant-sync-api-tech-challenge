package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class FindRestaurantByIdUseCaseImp implements FindRestaurantByIdUseCase{

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public FindRestaurantByIdUseCaseImp(RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Restaurant execute(Integer id) {
        this.userGateway.validateAdmin();
        return this.restaurantGateway.findRestaurantById(id);
    }
}
