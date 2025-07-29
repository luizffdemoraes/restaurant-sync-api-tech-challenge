package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;

public class FindMenuByIdUseCaseImp implements FindMenuByIdUseCase{

    private final MenuGateway menuGateway;
    private final UserGateway userGateway;

    public FindMenuByIdUseCaseImp(MenuGateway menuGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.userGateway = userGateway;
    }

    @Override
    public MenuResponse execute(Integer id) {
        this.userGateway.validateAdmin();
        Menu menu = this.menuGateway.findMenuById(id);
        return new MenuResponse(menu);
    }
}
