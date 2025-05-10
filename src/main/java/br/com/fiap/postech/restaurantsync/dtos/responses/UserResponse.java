package br.com.fiap.postech.restaurantsync.dtos.responses;

import br.com.fiap.postech.restaurantsync.entities.User;

import java.util.Date;

public record UserResponse(
        Long id,
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
