package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Role;

import java.util.Optional;

public interface RoleGateway {
    Optional<Role> findByAuthority(String authority);
}
