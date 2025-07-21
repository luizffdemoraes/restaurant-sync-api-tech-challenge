package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;

public interface UpdateUserUseCase {
    UserResponse execute(Integer id, UserRequest request);
}
