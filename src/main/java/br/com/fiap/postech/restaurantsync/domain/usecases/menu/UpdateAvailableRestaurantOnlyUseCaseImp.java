package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class UpdateAvailableRestaurantOnlyUseCaseImp implements UpdateAvailableRestaurantOnlyUseCase {

    private final MenuGateway menuGateway;
    private final UserGateway userGateway;

    public UpdateAvailableRestaurantOnlyUseCaseImp(MenuGateway menuGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Menu execute(Integer id, Boolean availableOnlyRestaurant) {
        this.userGateway.validateAdmin();
        return this.menuGateway.updateAvailableOnlyRestaurant(id, availableOnlyRestaurant);
    }
}
