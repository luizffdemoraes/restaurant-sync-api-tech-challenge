package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;

public interface UpdateAvailableRestaurantOnlyUseCase {
    MenuResponse execute(Integer id, Boolean availableOnlyRestaurant);
}
