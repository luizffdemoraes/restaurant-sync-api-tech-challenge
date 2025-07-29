package br.com.fiap.postech.restaurantsync.application.dtos.responses;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;

public record RestaurantResponse(
        Integer id,
        String name,
        AddressResponse address,
        String cuisineType,
        String openingHours,
        Integer ownerId) {

    public RestaurantResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
                new AddressResponse(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId()
        );
    }
}
