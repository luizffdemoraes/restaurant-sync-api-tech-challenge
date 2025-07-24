package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Address is required")
        AddressRequest address,

        @NotBlank(message = "Cuisine type is required")
        String cuisineType,

        @NotBlank(message = "Opening hours are required")
        String openingHours,

        @NotNull(message = "Owner ID is required")
        Integer ownerId) {

    public RestaurantRequest(String name, AddressRequest address, String cuisineType, String openingHours, Integer ownerId) {
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.ownerId = ownerId;
    }
}
