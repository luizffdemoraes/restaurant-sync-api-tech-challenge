package br.com.fiap.postech.restaurantsync.domain.usecases.menu;

import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface FindAllPagedMenuUseCase {
    Page<MenuResponse> execute(PageRequest pageRequest);
}
