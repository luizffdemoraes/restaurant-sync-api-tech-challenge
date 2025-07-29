package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class FindAllPagedRestaurantUseCaseImp implements FindAllPagedRestaurantUseCase{

    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public FindAllPagedRestaurantUseCaseImp(RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Page<RestaurantResponse> execute(PageRequest pageRequest) {
        this.userGateway.validateAdmin();
        Page<Restaurant> pagedRestaurants = this.restaurantGateway.findAllPagedRestaurants(pageRequest);
        return pagedRestaurants.map(RestaurantResponse::new);
    }
}
