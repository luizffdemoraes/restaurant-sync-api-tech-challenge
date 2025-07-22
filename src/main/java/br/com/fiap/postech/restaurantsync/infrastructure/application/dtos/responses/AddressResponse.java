package br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses;

import br.com.fiap.postech.restaurantsync.domain.entities.Address;

public record AddressResponse(
        String street,
        Long number,
        String city,
        String state,
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

