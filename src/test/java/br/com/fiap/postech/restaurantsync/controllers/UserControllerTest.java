package br.com.fiap.postech.restaurantsync.controllers;

import br.com.fiap.postech.restaurantsync.dtos.requests.PasswordRequest;
import br.com.fiap.postech.restaurantsync.dtos.requests.UserRequest;
import br.com.fiap.postech.restaurantsync.dtos.responses.UserResponse;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import br.com.fiap.postech.restaurantsync.resources.translator.FieldTranslationService;
import br.com.fiap.postech.restaurantsync.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FieldTranslationService translationService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testCreateUserSuccess() throws Exception {
        UserResponse userResponse = TestDataFactory.createUserResponse();
        UserRequest userRequest = TestDataFactory.createUserRequest();

        when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.nome").value(userResponse.name()))
                .andExpect(jsonPath("$.email").value(userResponse.email()))
                .andExpect(jsonPath("$.login").value(userResponse.login()))
                .andExpect(jsonPath("$.dataUltimaAtualizacao").exists())
                .andExpect(jsonPath("$.endereco").exists());
    }

    @Test
    void testCreateUserBadRequest() throws Exception {
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserSuccess() throws Exception {
        Integer userId = 1;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/v1/usuarios/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindAllPagedUsersSuccess() throws Exception {
        UserResponse userResponse = TestDataFactory.createUserResponse();
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));

        when(userService.findAllPagedUsers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/v1/usuarios")
                        .param("page", "0")
                        .param("linesPerPage", "12")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$.content[0].nome").value(userResponse.name()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindUserByIdSuccess() throws Exception {
        Integer userId = 1;
        UserResponse userResponse = TestDataFactory.createUserResponse();

        when(userService.findUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/v1/usuarios/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.nome").value(userResponse.name()));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        Integer userId = 1;
        UserRequest userRequest = TestDataFactory.createUserRequest();
        UserResponse userResponse = TestDataFactory.createUserResponse();

        when(userService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/v1/usuarios/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.nome").value(userResponse.name()));
    }

    @Test
    void testUpdatePasswordSuccess() throws Exception {
        Integer userId = 1;
        PasswordRequest passwordRequest = new PasswordRequest("novaSenha123");

        doNothing().when(userService).updatePassword(userId, passwordRequest.password());

        mockMvc.perform(patch("/v1/usuarios/{id}/senha", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isNoContent());
    }
}