package br.com.fiap.postech.restaurantsync.application.dtos.responses;


import br.com.fiap.postech.restaurantsync.domain.entities.Menu;

public record MenuResponse(
        Integer id,
        String name,
        String description,
        Double price,
        Boolean availableOnlyRestaurant,
        String photoPath,
        Integer restaurantId
) {
    public MenuResponse(Menu menuItem) {
        this(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isAvailableOnlyRestaurant(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId()
        );
    }
}