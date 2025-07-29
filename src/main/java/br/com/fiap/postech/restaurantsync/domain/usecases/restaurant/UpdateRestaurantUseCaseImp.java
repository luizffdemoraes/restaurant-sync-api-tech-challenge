package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
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
    public RestaurantResponse execute(Integer id, RestaurantRequest request) {
        this.userGateway.validateAdmin();
        Restaurant restaurantRequest = new Restaurant(request);
        if (request.ownerId() != null) {
            userGateway.validateUserByOwnerId(request.ownerId());
        }
        Restaurant updateRestaurant = this.restaurantGateway.updateRestaurant(id, restaurantRequest);
        return new RestaurantResponse(updateRestaurant);
    }
}
