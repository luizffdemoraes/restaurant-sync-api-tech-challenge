package br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity;

import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;

public class RestaurantEntity {
    private Integer id;
    private String name;
    private AddressEntity addressEntity;
    private String cuisineType;
    private String openingHours;
    private Integer ownerId;

    public RestaurantEntity() {
    }

    public RestaurantEntity(String name, AddressEntity address, String cuisineType, String openingHours, Integer ownerId) {
        this.name = name;
        this.addressEntity = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.ownerId = ownerId;
    }

    public static RestaurantEntity fromDomain(Restaurant restaurant) {
        if (restaurant == null) return null;
        var address = AddressEntity.fromDomain(restaurant.getAddress());
        return new RestaurantEntity(
                restaurant.getName(),
                address,
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId()
        );
    }

    public Restaurant toDomain() {
        return new Restaurant(
                this.name,
                this.addressEntity.toDomain(),
                this.cuisineType,
                this.openingHours,
                this.ownerId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}
