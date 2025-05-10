package br.com.fiap.postech.restaurantsync.dtos.requests;

import br.com.fiap.postech.restaurantsync.entities.User;
import br.com.fiap.postech.restaurantsync.resources.validations.UniqueValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados do request para criar usuário")
public record UserRequest(
        @JsonProperty("nome")
        @NotBlank(message = "Campo obrigatório")
        @Schema(description = "Nome do usuário", example = "John Doe")
        String name,

        @NotBlank(message = "Campo obrigatório")
        @Email(message = "Informar e-mail válido")
        @UniqueValue(domainClass = User.class, fieldName = "email", message = "Email já cadastrado.")
        @Schema(description = "Email do usuário", example = "johndoe@example.com")
        String email,

        @NotBlank(message = "Campo obrigatório")
        @Schema(description = "Login do usuário", example = "johndoe")
        String login,

        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("senha")
        @Schema(description = "Senha do usuário", example = "password123")
        String password,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        @JsonProperty("endereco")
        @Schema(description = "Endereço do usuário")
        AddressRequest address) {

    public UserRequest(String name, String email, String login, String password, AddressRequest address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.address = address;
    }
}
