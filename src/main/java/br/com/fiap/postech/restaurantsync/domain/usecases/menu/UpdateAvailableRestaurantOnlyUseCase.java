package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;

public interface UpdateAvailableRestaurantOnlyUseCase {
    Menu execute(Integer id, Boolean availableOnlyRestaurant);
}
