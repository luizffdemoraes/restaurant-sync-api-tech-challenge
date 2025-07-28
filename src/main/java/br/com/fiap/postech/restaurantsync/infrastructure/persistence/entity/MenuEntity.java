package br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "available_in_restaurant", nullable = false)
    private Boolean availableOnlyAtRestaurant;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    public MenuEntity() {

    }

    public MenuEntity(Integer id, String name, String description, Double price, boolean availableOnlyAtRestaurant, String photoPath, Integer restaurantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableOnlyAtRestaurant = availableOnlyAtRestaurant;
        this.photoPath = photoPath;
        this.restaurantId = restaurantId;
    }



    public Menu toDomain() {
        return new Menu(
                id,
                name,
                description,
                price,
                availableOnlyAtRestaurant != null && availableOnlyAtRestaurant, // evita null
                photoPath,
                restaurantId
        );
    }

    public static MenuEntity fromDomain(Menu menuItem) {
        return new MenuEntity(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isAvailableOnlyAtRestaurant(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId()
        );
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

    public Boolean getAvailableOnlyAtRestaurant() {
        return availableOnlyAtRestaurant;
    }

    public void setAvailableOnlyAtRestaurant(Boolean availableOnlyAtRestaurant) {
        this.availableOnlyAtRestaurant = availableOnlyAtRestaurant;
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