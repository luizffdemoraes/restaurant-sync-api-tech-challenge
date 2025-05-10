package br.com.fiap.postech.restaurantsync.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRequest(
        @JsonProperty("nome")
        String name,
        String email,
        String login,
        @JsonProperty("senha")
        String password,
        @JsonProperty("endereco")
        AddressRequest address) {

    public UserRequest(String name, String email, String login, String password, AddressRequest address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
    }
}
