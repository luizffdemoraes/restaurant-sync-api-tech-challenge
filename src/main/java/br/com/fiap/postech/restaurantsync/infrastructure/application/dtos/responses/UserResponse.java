package br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses;


import br.com.fiap.postech.restaurantsync.domain.entities.User;

import java.util.Date;

public record UserResponse(
        Integer id,
        String name,
        String email,
        String login,
        Date lastUpdateDate,
        AddressResponse address) {
    public UserResponse(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getLastUpdateDate(),
                new AddressResponse(user.getAddress())
        );
    }
}
