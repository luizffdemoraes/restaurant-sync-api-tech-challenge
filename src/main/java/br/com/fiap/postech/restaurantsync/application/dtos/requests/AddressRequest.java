package br.com.fiap.postech.restaurantsync.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record AddressRequest(
        @NotBlank(message = "Street is required")
        String street,

        @NotNull(message = "Number is required")
        Long number,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "ZipCode is required")
        String zipCode) {

    public AddressRequest(String street, Long number, String city, String state, String zipCode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
