package br.com.fiap.postech.restaurantsync.dtos.responses;

import br.com.fiap.postech.restaurantsync.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Dados do response para usuário")
public record UserResponse(
        @Schema(description = "ID do usuário", example = "1")
        Integer id,
        @JsonProperty("nome")
        @Schema(description = "Nome do usuário", example = "John Doe")
        String name,
        @Schema(description = "Email do usuário", example = "johndoe@example.com")
        String email,
        @Schema(description = "Login do usuário", example = "johndoe")
        String login,
        @JsonProperty("dataUltimaAtualizacao")
        @Schema(description = "Data da última atualização", example = "2023-10-01T12:00:00Z")
        Date lastUpdateDate,
        @JsonProperty("endereco")
        @Schema(description = "Endereço do usuário")
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
