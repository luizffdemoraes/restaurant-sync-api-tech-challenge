package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;

public interface FindRestaurantByIdUseCase {
    Restaurant execute(Integer id);
}
