package br.com.fiap.postech.restaurantsync.domain.usecases.restaurant;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindAllPagedRestaurantUseCase {
    Page<Restaurant> execute(PageRequest pageRequest);
}
