package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class CreateMenuUseCaseImp implements CreateMenuUseCase {

    private final MenuGateway menuGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateMenuUseCaseImp(MenuGateway menuGateway,
                                RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public MenuResponse execute(MenuRequest request) {
        this.userGateway.validateAdmin();
        restaurantGateway.findRestaurantById(request.restaurantId());
        Menu menu = new Menu(request);
        var savedMenu = menuGateway.saveRestaurant(menu);
        return new MenuResponse(savedMenu);
    }
}
