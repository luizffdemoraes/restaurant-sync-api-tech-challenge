package br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository;

import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer> {
}
