package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.domain.entities.User;
import br.com.fiap.postech.restaurantsync.domain.usecases.user.*;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.PasswordRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.infrastructure.config.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllPagedUsersUseCase findAllPagedUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          FindUserByIdUseCase findUserByIdUseCase,
                          FindAllPagedUsersUseCase findAllPagedUsersUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          UpdatePasswordUseCase updatePasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findAllPagedUsersUseCase = findAllPagedUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest request) {
        User user = UserMapper.toDomain(request);
        User userSave = this.createUserUseCase.execute(user);
        UserResponse response = UserMapper.toResponse(userSave);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        this.deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Integer id) {
        User user = this.findUserByIdUseCase.execute(id);
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAllPagedUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<User> list = this.findAllPagedUsersUseCase.execute(pageRequest);
        Page<UserResponse> response = list.map(UserMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest request) {
        User user = UserMapper.toDomain(request);
        User responseUpdate = updateUserUseCase.execute(id, user);
        UserResponse response = UserMapper.toResponse(responseUpdate);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, @Valid @RequestBody PasswordRequest request) {
        this.updatePasswordUseCase.execute(id, request.password());
        return ResponseEntity.noContent().build();
    }
}
