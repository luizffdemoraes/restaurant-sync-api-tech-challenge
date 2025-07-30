package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;

public interface CreateRestaurantUseCase {
    Restaurant execute(Restaurant request);
}
