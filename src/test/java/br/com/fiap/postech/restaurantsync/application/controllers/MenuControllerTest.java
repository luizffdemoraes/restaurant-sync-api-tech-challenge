package br.com.fiap.postech.restaurantsync.application.controllers;

import br.com.fiap.postech.restaurantsync.application.dtos.requests.MenuRequest;
import br.com.fiap.postech.restaurantsync.application.dtos.responses.MenuResponse;
import br.com.fiap.postech.restaurantsync.domain.entities.Menu;
import br.com.fiap.postech.restaurantsync.domain.usecases.menu.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateMenuUseCase createMenuUseCase;

    @MockBean
    private DeleteMenuUseCase deleteMenuUseCase;

    @MockBean
    private FindMenuByIdUseCase findMenuByIdUseCase;

    @MockBean
    private FindAllPagedMenuUseCase findAllPagedMenuUseCase;

    @MockBean
    private UpdateMenuUseCase updateMenuUseCase;

    @MockBean
    private UpdateAvailableRestaurantOnlyUseCase updateAvailableRestaurantOnlyUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateMenuSuccess() throws Exception {
        MenuRequest request = new MenuRequest(
                "Hambúrguer", "Sanduíche de carne", 35.50, true, "img/hb.png", 1
        );
        // Cria o domínio a partir do Request e seta o id
        Menu menu = new Menu(request);
        menu.setId(1);

        when(createMenuUseCase.execute(any())).thenReturn(menu);

        mockMvc.perform(post("/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/v1/menus/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hambúrguer"))
                .andExpect(jsonPath("$.description").value("Sanduíche de carne"))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.availableOnlyRestaurant").value(true))
                .andExpect(jsonPath("$.photoPath").value("img/hb.png"))
                .andExpect(jsonPath("$.restaurantId").value(1));
    }

    @Test
    void testCreateMenuBadRequest() throws Exception {
        // Falta campos obrigatórios, como name e price
        String invalidRequest = """
                {
                    "description":"desc",
                    "availableOnlyRestaurant":true,
                    "restaurantId":3
                }
                """;
        mockMvc.perform(post("/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMenuSuccess() throws Exception {
        doNothing().when(deleteMenuUseCase).execute(1);

        mockMvc.perform(delete("/v1/menus/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindAllPagedMenuSuccess() throws Exception {
        MenuResponse menu1 = new MenuResponse(1, "Pizza", "Pizza de calabresa", 45.00, false, "img/pizza.png", 2);
        MenuResponse menu2 = new MenuResponse(2, "Coxinha", "Coxinha de frango", 7.00, true, null, 1);

        when(findAllPagedMenuUseCase.execute(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(menu1, menu2), PageRequest.of(0, 12, Sort.Direction.ASC, "name"), 2));

        mockMvc.perform(get("/v1/menus")
                        .param("page", "0")
                        .param("linesPerPage", "12")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Pizza"))
                .andExpect(jsonPath("$.content[1].name").value("Coxinha"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFindMenuByIdSuccess() throws Exception {
        MenuResponse menu = new MenuResponse(1, "Pizza", "Pizza de calabresa", 45.00, false, "img/pizza.png", 2);
        when(findMenuByIdUseCase.execute(1)).thenReturn(menu);

        mockMvc.perform(get("/v1/menus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMenuSuccess() throws Exception {
        MenuRequest request = new MenuRequest("Novo Nome", "Nova Desc", 99.0, false, null, 2);
        MenuResponse response = new MenuResponse(1, "Novo Nome", "Nova Desc", 99.0, false, null, 2);

        when(updateMenuUseCase.execute(eq(1), any(MenuRequest.class))).thenReturn(response);

        mockMvc.perform(put("/v1/menus/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Novo Nome"));
    }

    @Test
    void testPartialUpdateMenuItemSuccess() throws Exception {
        // Aqui, simula a chamada de PATCH para alteração de availableOnlyRestaurant
        String body = """
                { "availableOnlyRestaurant":false }
                """;
        MenuResponse response = new MenuResponse(1, "Pizza", "Pizza de calabresa", 45.00, false, "img/pizza.png", 2);
        when(updateAvailableRestaurantOnlyUseCase.execute(eq(1), eq(false))).thenReturn(response);

        mockMvc.perform(patch("/v1/menus/1/restaurant-only")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableOnlyRestaurant").value(false));
    }
}
