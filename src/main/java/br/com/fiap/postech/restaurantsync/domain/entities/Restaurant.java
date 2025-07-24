package br.com.fiap.postech.restaurantsync.domain.entities;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;

public class Restaurant {
    private Integer id;
    private String name;
    private Address address;
    private String cuisineType;
    private String openingHours;
    private Integer ownerId;

    public Restaurant(String name, Address address, String cuisineType, String openingHours, Integer ownerId) {
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.openingHours = openingHours;
        this.ownerId = ownerId;
    }

    public Restaurant(RestaurantRequest request) {
        this.name = request.name();
        this.address = new Address(
                request.address().street(),
                request.address().number(),
                request.address().city(),
                request.address().state(),
                request.address().zipCode()
        );
        this.cuisineType = request.cuisineType();
        this.openingHours = request.openingHours();
        this.ownerId = request.ownerId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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


