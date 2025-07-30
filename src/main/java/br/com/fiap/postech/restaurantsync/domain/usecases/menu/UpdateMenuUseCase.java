package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import jakarta.validation.Valid;

public interface UpdateMenuUseCase {
    Menu execute(Integer id, @Valid Menu request);
}
