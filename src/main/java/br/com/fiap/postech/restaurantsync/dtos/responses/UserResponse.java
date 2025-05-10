package br.com.fiap.postech.restaurantsync.dtos.responses;

import br.com.fiap.postech.restaurantsync.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record UserResponse(
        Long id,
        @JsonProperty("nome")
        String name,
        String email,
        String login,
        @JsonProperty("dataUltimaAtualizacao")
        Date lastUpdateDate,
        @JsonProperty("endereco")
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
