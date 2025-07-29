package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import jakarta.validation.Valid;

public interface UpdateRestaurantUseCase {
    RestaurantResponse execute(Integer id, @Valid RestaurantRequest request);
}
