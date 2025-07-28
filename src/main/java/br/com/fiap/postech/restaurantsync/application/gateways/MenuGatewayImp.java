package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.gateways.MenuGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.MenuEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.MenuRepository;

public class MenuGatewayImp implements MenuGateway {

    private final MenuRepository menuItemRepository;

    public MenuGatewayImp(MenuRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public Menu saveRestaurant(Menu menuItem) {
        MenuEntity responseEntity = MenuEntity.fromDomain(menuItem);
        MenuEntity saved = this.menuItemRepository.save(responseEntity);
        return saved.toDomain();
    }
}
