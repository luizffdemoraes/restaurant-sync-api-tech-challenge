package br.com.fiap.postech.restaurantsync.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
    @NotBlank(message = "Campo obrigatório")
    @JsonProperty("senha")
    @Schema(description = "Senha do usuário", example = "password123")
    String password
){
    public PasswordRequest(String password) {
        this.password = password;
    }
}
