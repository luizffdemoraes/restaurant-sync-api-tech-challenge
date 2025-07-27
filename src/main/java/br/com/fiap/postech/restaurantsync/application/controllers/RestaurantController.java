package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.domain.usecases.restaurant.CreateRestaurantUseCase;
import br.com.fiap.postech.restaurantsync.domain.usecases.restaurant.DeleteRestaurantUseCase;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
   // private final FindRestaurantByIdUseCase findRestaurantByIdUseCase;
   // private final FindAllPagedRestaurantUseCase findAllPagedRestaurantUseCase;
   // private final UpdateRestaurantUseCase updateRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                DeleteRestaurantUseCase deleteRestaurantUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase,
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = this.createRestaurantUseCase.execute(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        this.deleteRestaurantUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
