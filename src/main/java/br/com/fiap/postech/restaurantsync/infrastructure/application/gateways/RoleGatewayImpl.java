package br.com.fiap.postech.restaurantsync.infrastructure.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.RoleEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.repository.RoleRepository;

import java.util.Optional;

public class RoleGatewayImpl implements RoleGateway {

    private final RoleRepository roleRepository;

    public RoleGatewayImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByAuthority(String authority) {
        return roleRepository.findByAuthority(authority)
                .map(RoleEntity::toDomain);
    }
}
