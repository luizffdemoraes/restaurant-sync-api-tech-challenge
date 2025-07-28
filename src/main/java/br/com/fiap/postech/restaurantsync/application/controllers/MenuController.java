package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.usecases.menu.CreateMenuUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/menu-items")
public class MenuController {

    private final CreateMenuUseCase createMenuItemUseCase;

    public MenuController(CreateMenuUseCase createMenuItemUseCase) {
        this.createMenuItemUseCase = createMenuItemUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createMenu(@Valid @RequestBody MenuRequest request) {
        MenuResponse response = this.createMenuItemUseCase.execute(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
