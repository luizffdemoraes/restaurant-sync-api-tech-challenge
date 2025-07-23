package br.com.fiap.postech.restaurantsync.application.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.Role;
import br.com.fiap.postech.restaurantsync.domain.gateways.RoleGateway;
import br.com.fiap.postech.restaurantsync.infrastructure.exceptions.BusinessException;
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

    public Role getRoleForEmail(String email) {
        if (email != null && email.toLowerCase().endsWith("@restaurantsync.com")) {
            return roleRepository.findByAuthority("ROLE_ADMIN")
                    .orElseThrow(() -> new BusinessException("Role ADMIN não encontrada.")).toDomain();
        } else {
            return roleRepository.findByAuthority("ROLE_CLIENT")
                    .orElseThrow(() -> new BusinessException("Role CLIENT não encontrada.")).toDomain();
        }
    }
}
