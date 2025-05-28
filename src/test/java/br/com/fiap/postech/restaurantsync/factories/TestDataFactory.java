package br.com.fiap.postech.restaurantsync.factories;

import br.com.fiap.postech.restaurantsync.dtos.requests.AddressRequest;
import br.com.fiap.postech.restaurantsync.dtos.requests.PasswordRequest;
import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.AddressResponse;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;

import java.util.Date;

public class TestDataFactory {

    public static UserRequest createUserRequest() {
        return new UserRequest(
                "John Doe",
                "johndoe@example.com",
                "johndoe",
                "senha123",
                createAddressRequest()
        );
    }

    public static AddressRequest createAddressRequest() {
        return new AddressRequest(
                "Rua Exemplo",
                587L,
                "São Paulo",
                "SP",
                "12345-678"
        );
    }

    public static UserResponse createUserResponse() {
        return new UserResponse(
                1L,
                "John Doe",
                "johndoe@example.com",
                "johndoe",
                new Date(),
                createAddressResponse()
        );
    }

    public static AddressResponse createAddressResponse() {
        return new AddressResponse(
                "Rua Exemplo",
                587L,
                "São Paulo",
                "SP",
                "12345-678"
        );
    }

    public static PasswordRequest createPasswordRequest() {
        return new PasswordRequest("novaSenha123");
    }
}