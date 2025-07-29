package br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository;

import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
}
