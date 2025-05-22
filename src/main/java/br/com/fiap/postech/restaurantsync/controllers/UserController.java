package br.com.fiap.postech.restaurantsync.controllers;

import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Endpoint de Usuários", description = "Endpoints para operações de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Criar novo usuário",
            description = "Cria um novo usuário e retorna os dados do usuário criado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "422",
                    description = "Erro de validação dos campos")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(userResponse.id()).toUri();
        return ResponseEntity.created(uri).body(userResponse);
    }

    @Operation(summary = "Deletar usuário",
            description = "Remove um usuário existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400",
                    description = "ID inválido")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar usuários",
            description = "Retorna uma lista paginada de usuários com opções de ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de usuários recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400",
                    description = "Parâmetros de paginação inválidos")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAllPagedUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        Page<UserResponse> list = userService.findAllPagedUsers(pageRequest);

        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        UserResponse response = userService.findUserById(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Atualizar usuário",
            description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400",
                    description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "422",
                    description = "Erro de validação dos campos")
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Atualizar senha",
            description = "Atualiza a senha de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400",
                    description = "Nova senha inválida")
    })
    @PatchMapping(value = "/{id}/senha")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }
}
