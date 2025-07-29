package br.com.fiap.postech.restaurantsync.domain.entities;


import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;

public class Menu {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private boolean availableOnlyRestaurant;
    private String photoPath;
    private Integer restaurantId;


    public Menu(Integer id, String name, String description, Double price,
                boolean availableOnlyRestaurant, String photoPath,
                Integer restaurantIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableOnlyRestaurant = availableOnlyRestaurant;
        this.photoPath = photoPath;
        this.restaurantId = restaurantIds;
    }

    public Menu(MenuRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.availableOnlyRestaurant = request.availableOnlyRestaurant();
        this.photoPath = request.photoPath();
        this.restaurantId = request.restaurantId();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isAvailableOnlyRestaurant() {
        return availableOnlyRestaurant;
    }

    public void setAvailableOnlyRestaurant(boolean availableOnlyRestaurant) {
        this.availableOnlyRestaurant = availableOnlyRestaurant;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }
}