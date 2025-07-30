package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.AvailableOnlyRestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.usecases.menu.*;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.MenuMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/menus")
public class MenuController {

    private final CreateMenuUseCase createMenuUseCase;
    private final DeleteMenuUseCase deleteMenuUseCase;
    private final FindMenuByIdUseCase findMenuByIdUseCase;
    private final FindAllPagedMenuUseCase findAllPagedMenuUseCase;
    private final UpdateMenuUseCase updateMenuUseCase;
    private final UpdateAvailableRestaurantOnlyUseCase updateAvailableRestaurantOnlyUseCase;


    public MenuController(CreateMenuUseCase createMenuUseCase, DeleteMenuUseCase deleteMenuUseCase, FindMenuByIdUseCase findMenuByIdUseCase, FindAllPagedMenuUseCase findAllPagedMenuUseCase, UpdateMenuUseCase updateMenuUseCase, UpdateAvailableRestaurantOnlyUseCase updateAvailableRestaurantOnlyUseCase) {
        this.createMenuUseCase = createMenuUseCase;
        this.deleteMenuUseCase = deleteMenuUseCase;
        this.findMenuByIdUseCase = findMenuByIdUseCase;
        this.findAllPagedMenuUseCase = findAllPagedMenuUseCase;
        this.updateMenuUseCase = updateMenuUseCase;
        this.updateAvailableRestaurantOnlyUseCase = updateAvailableRestaurantOnlyUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createMenu(@Valid @RequestBody MenuRequest request) {
        Menu menu = this.createMenuUseCase.execute(MenuMapper.toDomain(request));
        MenuResponse response = MenuMapper.toResponse(menu);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Integer id) {
        this.deleteMenuUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findMenuById(@PathVariable Integer id) {
        MenuResponse response = this.findMenuByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<MenuResponse>> findAllPagedMenu(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<MenuResponse> list = this.findAllPagedMenuUseCase.execute(pageRequest);
        return ResponseEntity.ok(list);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable Integer id, @Valid @RequestBody MenuRequest request) {
        MenuResponse response = this.updateMenuUseCase.execute(id, request);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/restaurant-only")
    public ResponseEntity<MenuResponse> partialUpdateMenuItem(
            @PathVariable Integer id,
            @Valid @RequestBody AvailableOnlyRestaurantRequest request) {
        MenuResponse response = this.updateAvailableRestaurantOnlyUseCase.execute(id, request.availableOnlyRestaurant());
        return ResponseEntity.ok().body(response);
    }
}
