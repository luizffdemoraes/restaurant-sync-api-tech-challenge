package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Restaurant;
import br.com.fiap.postech.restaurantsync.domain.usecases.restaurant.*;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.RestaurantMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
    private final FindRestaurantByIdUseCase findRestaurantByIdUseCase;
    private final FindAllPagedRestaurantUseCase findAllPagedRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase,
                                FindRestaurantByIdUseCase findRestaurantByIdUseCase,
                                FindAllPagedRestaurantUseCase findAllPagedRestaurantUseCase,
                                UpdateRestaurantUseCase updateRestaurantUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
        this.findRestaurantByIdUseCase = findRestaurantByIdUseCase;
        this.findAllPagedRestaurantUseCase = findAllPagedRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        Restaurant restaurant = RestaurantMapper.toDomain(request);
        Restaurant response = this.createRestaurantUseCase.execute(restaurant);
        RestaurantResponse restaurantResponse = RestaurantMapper.toResponse(response);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(restaurantResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        this.deleteRestaurantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findRestaurantById(@PathVariable Integer id) {
        Restaurant response = this.findRestaurantByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponse>> findAllPagedRestaurant(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<Restaurant> list = this.findAllPagedRestaurantUseCase.execute(pageRequest);
        Page<RestaurantResponse> response = list.map(RestaurantMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable Integer id, @Valid @RequestBody RestaurantRequest request) {
        Restaurant restaurant = RestaurantMapper.toDomain(request);
        Restaurant responseUpdate = this.updateRestaurantUseCase.execute(id, restaurant);
        RestaurantResponse response = RestaurantMapper.toResponse(responseUpdate);
        return ResponseEntity.ok().body(response);
    }
}
