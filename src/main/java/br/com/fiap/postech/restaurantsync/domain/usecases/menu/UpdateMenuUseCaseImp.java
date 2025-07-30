package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.RestaurantGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class UpdateMenuUseCaseImp implements UpdateMenuUseCase{

    private final MenuGateway menuGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;


    public UpdateMenuUseCaseImp(MenuGateway menuGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Menu execute(Integer id, Menu menu) {
        this.userGateway.validateAdmin();
        restaurantGateway.findRestaurantById(menu.getRestaurantId());
        return this.menuGateway.updateMenu(id, menu);
    }
}
