package br.com.fiap.postech.restaurantsync.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record AddressRequest(
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("rua")
        @Schema(description = "Rua do endereço", example = "Rua das Flores")
        String street,

        @NotNull(message = "Campo obrigatório")
        @JsonProperty("numero")
        @Schema(description = "Número do endereço", example = "123")
        Long number,

        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("cidade")
        @Schema(description = "Cidade do endereço", example = "São Paulo")
        String city,

        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("estado")
        @Schema(description = "Estado do endereço", example = "SP")
        String state,

        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("cep")
        @Schema(description = "CEP do endereço", example = "12345-678")
        String zipCode) {

    public AddressRequest(String street, Long number, String city, String state, String zipCode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
