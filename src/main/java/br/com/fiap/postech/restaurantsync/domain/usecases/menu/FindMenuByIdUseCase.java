package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;

public interface FindMenuByIdUseCase {
    Menu execute(Integer id);
}
