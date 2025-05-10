package br.com.fiap.postech.restaurantsync.dtos.responses;

import br.com.fiap.postech.restaurantsync.entities.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressResponse(
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

