package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import jakarta.validation.Valid;

public interface UpdateMenuUseCase {
    MenuResponse execute(Integer id, @Valid MenuRequest request);
}
