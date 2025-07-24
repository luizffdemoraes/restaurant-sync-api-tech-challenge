package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;

public class CreateRestaurantUseCaseImp implements CreateRestaurantUseCase{

    private final RestaurantGateway restaurantGateway;

    public CreateRestaurantUseCaseImp(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    @Override
    public RestaurantResponse execute(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request);
        Restaurant savedRestaurant = restaurantGateway.saveRestaurant(restaurant);
        return new RestaurantResponse(savedRestaurant);
    }
}
