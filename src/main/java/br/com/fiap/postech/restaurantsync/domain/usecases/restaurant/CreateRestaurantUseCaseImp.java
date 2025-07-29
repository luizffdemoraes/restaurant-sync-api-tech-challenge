package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class CreateRestaurantUseCaseImp implements CreateRestaurantUseCase{

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateRestaurantUseCaseImp(RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public RestaurantResponse execute(RestaurantRequest request) {
        userGateway.validateUserByOwnerId(request.ownerId());
        Restaurant restaurant = new Restaurant(request);
        Restaurant savedRestaurant = restaurantGateway.saveRestaurant(restaurant);
        return new RestaurantResponse(savedRestaurant);
    }
}
