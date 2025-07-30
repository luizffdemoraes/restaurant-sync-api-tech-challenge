package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;

public interface UpdateUserUseCase {
    User execute(Integer id, User request);
}
