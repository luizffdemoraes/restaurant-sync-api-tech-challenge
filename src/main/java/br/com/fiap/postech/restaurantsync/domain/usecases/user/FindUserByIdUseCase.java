package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;

public interface FindUserByIdUseCase {
    UserResponse execute(Integer id);
}
