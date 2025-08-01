package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MenuGateway {
    Menu saveRestaurant(Menu menuItem);
    void deleteMenuById(Integer id);
    Page<Menu> findAllPagedMenus(PageRequest pageRequest);
    Menu findMenuById(Integer id);
    Menu updateMenu(Integer id, Menu menu);
    Menu updateAvailableOnlyRestaurant(Integer id, Boolean availableOnlyRestaurant);
    Menu findMenuOrThrow(Integer id);
}
