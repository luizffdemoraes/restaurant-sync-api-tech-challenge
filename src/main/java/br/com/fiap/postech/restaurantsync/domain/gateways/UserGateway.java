package br.com.fiap.postech.restaurantsync.domain.gateways;

import br.com.fiap.postech.restaurantsync.domain.entities.User;

public interface UserGateway {
    User save(User user);
    boolean existsByEmail(String email);
}