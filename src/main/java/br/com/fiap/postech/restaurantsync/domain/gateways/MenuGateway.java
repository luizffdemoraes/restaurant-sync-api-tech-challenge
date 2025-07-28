package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;

public interface MenuGateway {
    Menu saveRestaurant(Menu menuItem);
}
