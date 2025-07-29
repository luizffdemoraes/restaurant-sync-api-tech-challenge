package br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository;

import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByAuthority(String roleAdmin);
}
