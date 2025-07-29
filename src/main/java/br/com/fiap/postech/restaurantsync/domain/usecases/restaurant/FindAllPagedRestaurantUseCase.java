package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindAllPagedRestaurantUseCase {
    Page<RestaurantResponse> execute(PageRequest pageRequest);
}
