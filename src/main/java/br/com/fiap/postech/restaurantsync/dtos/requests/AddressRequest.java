package br.com.fiap.postech.restaurantsync.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("rua")
        String street,
        @NotNull(message = "Campo obrigatório")
        @JsonProperty("numero")
        Long number,
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("cidade")
        String city,
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("estado")
        String state,
        @NotBlank(message = "Campo obrigatório")
        @JsonProperty("cep")
        String zipCode) {

    public AddressRequest(String street, Long number, String city, String state, String zipCode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
