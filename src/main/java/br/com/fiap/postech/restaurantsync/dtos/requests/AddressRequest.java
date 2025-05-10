package br.com.fiap.postech.restaurantsync.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressRequest(
        @JsonProperty("rua")
        String street,
        @JsonProperty("numero")
        Long number,
        @JsonProperty("cidade")
        String city,
        @JsonProperty("estado")
        String state,
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
