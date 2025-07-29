package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;

public interface FindMenuByIdUseCase {
    MenuResponse execute(Integer id);
}
