package br.com.fiap.postech.restaurantsync.infrastructure.config.mapper;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RestaurantEntity;

public class RestaurantMapper {

    public static Restaurant toDomain(RestaurantRequest request) {
        return new Restaurant(request.name(),
                AddressMapper.toDomain(request.address()),
                request.cuisineType(),
                request.openingHours(),
                request.ownerId());
    }

    public static RestaurantResponse toResponse(Restaurant domain) {
        return new RestaurantResponse(domain);
    }

    public static Restaurant toDomain(RestaurantEntity entity) {
        return new Restaurant(
                entity.getId(),
                entity.getName(),
                entity.getAddressEntity().toDomain(),
                entity.getCuisineType(),
                entity.getOpeningHours(),
                entity.getOwnerId());
    }

    public static RestaurantEntity toEntity(Restaurant domain) {
        if (domain == null) return null;
        var address = AddressMapper.fromDomain(domain.getAddress());
        return new RestaurantEntity(
                domain.getId(),
                domain.getName(),
                address,
                domain.getCuisineType(),
                domain.getOpeningHours(),
                domain.getOwnerId()
        );
    }
}
