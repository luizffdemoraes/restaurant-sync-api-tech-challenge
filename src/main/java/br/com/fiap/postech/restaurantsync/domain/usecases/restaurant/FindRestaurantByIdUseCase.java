package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;

public interface FindRestaurantByIdUseCase {
    RestaurantResponse execute(Integer id);
}
