package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class CreateMenuUseCaseImp implements CreateMenuUseCase {

    private final MenuGateway menuGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;

    public CreateMenuUseCaseImp(MenuGateway menuGateway,
                                RestaurantGateway restaurantGateway,
                                UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Menu execute(Menu menu) {
        this.userGateway.validateAdmin();
        restaurantGateway.findRestaurantById(menu.getRestaurantId());
        return menuGateway.saveRestaurant(menu);
    }
}
