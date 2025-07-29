package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;

public interface CreateUserUseCase {
    UserResponse execute(UserRequest userRequest);
}