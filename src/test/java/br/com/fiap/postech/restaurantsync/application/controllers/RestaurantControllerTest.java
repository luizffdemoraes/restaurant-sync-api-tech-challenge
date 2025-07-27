package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.RestaurantRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.RestaurantResponse;
import br.com.fiap.postech.restaurantsync.domain.usecases.restaurant.*;
import br.com.fiap.postech.restaurantsync.factories.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static br.com.fiap.postech.restaurantsync.factories.TestDataFactory.createRestaurantRequest;
import static br.com.fiap.postech.restaurantsync.factories.TestDataFactory.createRestaurantResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRestaurantUseCase createRestaurantUseCase;

    @MockBean
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @MockBean
    private FindRestaurantByIdUseCase findRestaurantByIdUseCase;

    @MockBean
    private FindAllPagedRestaurantUseCase findAllPagedRestaurantUseCase;

    @MockBean
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRestaurantSuccess() throws Exception {
        RestaurantRequest request = createRestaurantRequest();
        RestaurantResponse response = createRestaurantResponse();
        when(createRestaurantUseCase.execute(any(RestaurantRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.cuisineType").value(request.cuisineType()));
    }

    @Test
    void testCreateRestaurantBadRequest() throws Exception {
        RestaurantRequest invalidRequest = new RestaurantRequest(
                "", // nome vazio, inválido
                TestDataFactory.createAddressRequest(),
                "Japonesa",
                "wrong-interval", // formato de hora inválido
                1
        );

        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetRestaurantByIdSuccess() throws Exception {
        int id = 1;
        RestaurantResponse response = createRestaurantResponse();
        when(findRestaurantByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/v1/restaurants/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.cuisineType").value(response.cuisineType()));
    }

    @Test
    void testListPagedRestaurants() throws Exception {
        RestaurantResponse response = createRestaurantResponse();
        Page<RestaurantResponse> mockPage = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10, Sort.Direction.ASC, "name"),
                1
        );

        // Mock do use case para aceitar PageRequest (como na implementação real)
        when(findAllPagedRestaurantUseCase.execute(any(PageRequest.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/v1/restaurants")
                        .param("page", "0")
                        .param("linesPerPage", "10")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id()))
                .andExpect(jsonPath("$.content[0].name").value(response.name()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testUpdateRestaurantSuccess() throws Exception {
        int id = 1;
        RestaurantRequest request = createRestaurantRequest();
        RestaurantResponse response = createRestaurantResponse();

        when(updateRestaurantUseCase.execute(eq(id), any(RestaurantRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/restaurants/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteRestaurantSuccess() throws Exception {
        int id = 1;
        doNothing().when(deleteRestaurantUseCase).execute(id);

        mockMvc.perform(delete("/v1/restaurants/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteRestaurantNotFound() throws Exception {
        int id = 999;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found!"))
                .when(deleteRestaurantUseCase).execute(id);

        mockMvc.perform(delete("/v1/restaurants/{id}", id))
                .andExpect(status().isNotFound());
    }
}
