package br.com.fiap.postech.restaurantsync.dtos.requests;

import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.resources.validations.UniqueValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @JsonProperty("nome")
        @NotBlank(message = "Campo obrigatório")
        String name,
        @NotBlank(message = "Campo obrigatório")
        @Email(message = "Informar e-mail válido")
        @UniqueValue(domainClass = User.class, fieldName = "email", message = "Email já cadastrado.")
        String email,
        @NotBlank(message = "Campo obrigatório")
        String login,
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("senha")
        String password,
        @Valid
        @NotNull(message = "Endereço é obrigatório")
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
