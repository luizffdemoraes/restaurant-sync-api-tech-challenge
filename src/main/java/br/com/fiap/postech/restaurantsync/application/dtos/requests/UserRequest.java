package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.UserEntity;
import br.com.fiap.postech.restaurantsync.infrastructure.validations.UniqueValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UserRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "E" +
                "mail is required")
        @Email(message = "Provide a valid email")
        @UniqueValue(domainClass = UserEntity.class, fieldName = "email", message = "Email already registered.")
        String email,

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password,

        @Valid
        @NotNull(message = "Address is required")
        AddressRequest address) {

    public UserRequest(String name, String email, String login, String password, AddressRequest address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
    }
}
