package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.infrastructure.application.dtos.responses.UserResponse;

public interface FindUserByIdUseCase {
    UserResponse execute(Integer id);
}
