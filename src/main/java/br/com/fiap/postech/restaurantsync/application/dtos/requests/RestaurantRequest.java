package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import br.com.fiap.postech.restaurantsync.infrastructure.validations.ValidTimeInterval;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Address is required")
        AddressRequest address,

        @NotBlank(message = "Cuisine type is required")
        String cuisineType,

        @ValidTimeInterval
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$",
                message = "Opening hours must be in format 'HH:MM-HH:MM' with valid times (ex: '09:00-18:00')")
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
