package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record AvailableOnlyRestaurantRequest(
        @NotNull(message = "Availability is required")
        Boolean availableOnlyRestaurant
) {
}
