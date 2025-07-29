package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
    @NotBlank(message = "Password is required")
    String password
){
    public PasswordRequest(String password) {
        this.password = password;
    }
}
