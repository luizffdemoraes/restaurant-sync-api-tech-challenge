package br.com.fiap.postech.restaurantsync.infrastructure.config.mapper;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.AddressRequest;
import br.com.fiap.postech.restaurantsync.domain.entities.Address;
import br.com.fiap.postech.restaurantsync.infrastructure.persistence.entity.AddressEntity;

public class AddressMapper {

    public static AddressEntity fromDomain(Address address) {
        if (address == null) return null;
        return new AddressEntity(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }

    public static Address toDomain(AddressRequest address) {
        if (address == null) return null;
        return new Address(
                address.street(),
                address.number(),
                address.city(),
                address.state(),
                address.zipCode()
        );
    }
}
