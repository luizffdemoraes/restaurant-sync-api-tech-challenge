package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.domain.gateways.UserGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class FindAllPagedMenuUseCaseImp implements FindAllPagedMenuUseCase{

    private final MenuGateway menuGateway;
    private final UserGateway userGateway;

    public FindAllPagedMenuUseCaseImp(MenuGateway menuGateway, UserGateway userGateway) {
        this.menuGateway = menuGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Page<MenuResponse> execute(PageRequest pageRequest) {
        this.userGateway.validateAdmin();
        Page<Menu> pagedMenus = this.menuGateway.findAllPagedMenus(pageRequest);
        return pagedMenus.map(MenuResponse::new);
    }
}
