package br.com.fiap.postech.restaurantsync.repositories;

import br.com.fiap.postech.restaurantsync.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String roleAdmin);
}
