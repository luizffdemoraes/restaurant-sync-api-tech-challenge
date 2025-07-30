package br.com.fiap.postech.restaurantsync.infrastructure.config.mapper;


import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.MenuEntity;

public class MenuMapper {

    public static Menu toDomain(MenuRequest request) {
        return new Menu(
                request.name(),
                request.description(),
                request.price(),
                request.availableOnlyRestaurant() != null && request.availableOnlyRestaurant(),
                request.photoPath(),
                request.restaurantId());
    }

    public static MenuResponse toResponse(Menu domain) {
        return new MenuResponse(domain);
    }

    public static Menu toDomain(MenuEntity entity) {
        return new Menu(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getAvailableOnlyRestaurant() != null && entity.getAvailableOnlyRestaurant(),
                entity.getPhotoPath(),
                entity.getRestaurantId()
        );
    }

    public static MenuEntity toEntity(Menu domain) {
        return new MenuEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.isAvailableOnlyRestaurant(),
                domain.getPhotoPath(),
                domain.getRestaurantId()
        );
    }
}

