package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import jakarta.validation.Valid;

public interface UpdateRestaurantUseCase {
    Restaurant execute(Integer id, @Valid Restaurant request);
}
