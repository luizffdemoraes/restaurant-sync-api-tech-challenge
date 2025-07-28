package br.com.fiap.postech.restaurantsync.application.dtos.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MenuRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        @NotNull(message = "Availability is required")
        Boolean availableOnlyAtRestaurant,

        String photoPath,

        @NotNull(message = "Restaurant IDs are required")
        @Positive(message = "Restaurant ID must be positive")
        Integer restaurantId
) {
    public MenuRequest(String name, String description, Double price, Boolean availableOnlyAtRestaurant, String photoPath, Integer restaurantId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableOnlyAtRestaurant = availableOnlyAtRestaurant;
        this.photoPath = photoPath;
        this.restaurantId = restaurantId;
    }
}