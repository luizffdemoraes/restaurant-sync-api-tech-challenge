package br.com.fiap.postech.restaurantsync.domain.usecases.user;

import br.com.fiap.postech.restaurantsync.domain.entities.User;

public interface CreateUserUseCase {
    User execute(User user);
}