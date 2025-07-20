package br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses;

import br.com.fiap.postech.restaurantsync.domain.entities.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record AddressResponse(
        @JsonProperty("rua")
        @Schema(description = "Rua do endereço", example = "Rua das Flores")
        String street,

        @JsonProperty("numero")
        @Schema(description = "Número do endereço", example = "123")
        Long number,

        @JsonProperty("cidade")
        @Schema(description = "Cidade do endereço", example = "São Paulo")
        String city,

        @JsonProperty("estado")
        @Schema(description = "Estado do endereço", example = "SP")
        String state,

        @JsonProperty("cep")
        @Schema(description = "CEP do endereço", example = "12345-678")
        String zipCode) {

    public AddressResponse(Address address) {
        this(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }
}

