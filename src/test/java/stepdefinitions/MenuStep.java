package stepdefinitions;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MenuStep {

    @Autowired
    private TestRestTemplate restTemplate;

    private Map<String, Object> menuData;
    private Map<String, Object> updateMenuData;
    private ResponseEntity<Map> menuResponse;
    private ResponseEntity<Map> menusListResponse;
    private ResponseEntity<Map> menuByIdResponse;
    private ResponseEntity<Map> updateMenuResponse;
    private static Integer createdMenuId;


    @Quando("eu envio uma requisição POST para {string} com os dados do menu")
    public void eu_envio_uma_requisicao_POST_para_com_os_dados_do_menu(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(menuData, headers);
        menuResponse = restTemplate.postForEntity(endpoint, request, Map.class);
    }

    @Então("o corpo da resposta deve conter os dados do menu criado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_menu_criado() {
        assertThat(menuResponse.getStatusCodeValue(), equalTo(201));

        Map<String, Object> body = menuResponse.getBody();
        assertThat(body.get("id"), notNullValue());
        assertThat(body.get("name"), equalTo(menuData.get("name")));
        assertThat(body.get("description"), equalTo(menuData.get("description")));
        assertThat(body.get("price"), equalTo(menuData.get("price")));
        assertThat(body.get("availableOnlyRestaurant"), equalTo(menuData.get("availableOnlyRestaurant")));
        assertThat(body.get("photoPath"), equalTo(menuData.get("photoPath")));
        assertThat(body.get("restaurantId"), equalTo(menuData.get("restaurantId")));
    }

    @Então("a resposta do menu deve ter status {int}")
    public void a_resposta_do_menu_deve_ter_status(int expectedStatus) {
        ResponseEntity<?> response = updateMenuResponse != null ? updateMenuResponse
                : menuByIdResponse != null ? menuByIdResponse
                : menusListResponse != null ? menusListResponse
                : menuResponse;
        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(expectedStatus));
    }

    @Quando("eu consulto a lista de menus em {string}")
    public void eu_consulto_lista_de_menus_em(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        menusListResponse = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                request,
                Map.class
        );
    }

    @Então("o corpo da resposta deve conter a lista de itens de menu")
    public void o_corpo_da_resposta_deve_conter_a_lista_de_itens_de_menu() {
        Map<String, Object> body = menusListResponse.getBody();
        assertThat(body, notNullValue());
        assertThat(body.containsKey("content"), is(true));
        assertThat(body.get("content"), instanceOf(List.class));
    }

    @Quando("eu consulto o item de menu com ID {int} em {string}")
    public void eu_consulto_item_menu_por_id(int id, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        String url = endpoint.replace("{id}", String.valueOf(id));
        menuByIdResponse = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    @Então("o corpo da resposta deve conter os dados do item de menu consultado")
    public void o_corpo_da_resposta_deve_conter_dados_item_menu_consultado() {
        Map<String, Object> body = menuByIdResponse.getBody();
        assertThat(body.get("id"), equalTo(1));
        assertThat(body.get("name"), notNullValue());
        assertThat(body.get("description"), notNullValue());
        assertThat(body.get("price"), notNullValue());
        assertThat(body.get("availableOnlyRestaurant"), notNullValue());
        assertThat(body.get("photoPath"), notNullValue());
        assertThat(body.get("restaurantId"), notNullValue());
    }

    @Dado("eu tenho os dados do item de menu {string}")
    public void eu_tenho_os_dados_do_item_de_menu(String name) {
        menuData = new HashMap<>();
        menuData.put("name", name);
        menuData.put("description", "Feijoada pequena com todas as acompanhamentos");
        menuData.put("price", 40.00);
        menuData.put("availableOnlyRestaurant", true);
        menuData.put("photoPath", "/images/feijoada.jpg");
        menuData.put("restaurantId", 1);
    }

    @Quando("eu envio uma requisição PUT para {string} com os dados atualizados do menu")
    public void eu_envio_requisicao_PUT_para_com_dados_atualizados_do_menu(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateMenuData, headers);
        String url = endpoint.replace("{id}", "1");
        updateMenuResponse = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
    }

    @Dado("eu tenho os dados atualizados do item de menu")
    public void eu_tenho_os_dados_atualizados_do_item_de_menu() {
        updateMenuData = new HashMap<>();
        updateMenuData.put("name", "Feijoada");
        updateMenuData.put("description", "Feijoada");
        updateMenuData.put("price", 45.00);
        updateMenuData.put("availableOnlyRestaurant", true);
        updateMenuData.put("photoPath", "/images/feijoada.jpg");
        updateMenuData.put("restaurantId", 1);
    }

    @Então("o corpo da resposta deve conter os dados do menu atualizado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_menu_atualizado() {
        Map<String, Object> body = updateMenuResponse.getBody();
        assertThat(body.get("id"), equalTo(1));
        assertThat(body.get("name"), equalTo(updateMenuData.get("name")));
        assertThat(body.get("description"), equalTo(updateMenuData.get("description")));
        assertThat(body.get("price"), equalTo(updateMenuData.get("price")));
        assertThat(body.get("availableOnlyRestaurant"), equalTo(updateMenuData.get("availableOnlyRestaurant")));
        assertThat(body.get("photoPath"), equalTo(updateMenuData.get("photoPath")));
        assertThat(body.get("restaurantId"), equalTo(updateMenuData.get("restaurantId")));
    }

    @Então("o corpo da resposta deve conter \"availableOnlyRestaurant\" igual a {word}")
    public void verifica_disponibilidade(String expectedStr) {
        boolean expected = Boolean.parseBoolean(expectedStr);
        Map<String, Object> body = updateMenuResponse.getBody();
        assertThat(body.get("availableOnlyRestaurant"), equalTo(expected));
    }

    @Dado("o item de menu {string} está cadastrado")
    public void o_item_de_menu_esta_cadastrado(String name) {
        menuData = new HashMap<>();
        menuData.put("name", name);
        menuData.put("description", "Feijoada pequena com todas as acompanhamentos");
        menuData.put("price", 40.00);
        menuData.put("availableOnlyRestaurant", true);
        menuData.put("photoPath", "/images/feijoada.jpg");
        menuData.put("restaurantId", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(menuData, headers);
        menuResponse = restTemplate.postForEntity("/v1/menus", request, Map.class);

        assertThat(menuResponse.getStatusCodeValue(), equalTo(201));
        createdMenuId = (Integer) menuResponse.getBody().get("id");
    }

    @Quando("eu envio uma requisição PATCH para {string} com availableOnlyRestaurant {booleanValue}")
    public void eu_envio_requisicao_PATCH_com_disponibilidade(String endpoint, Boolean availableOnlyRestaurant) {
        String url = endpoint.replace("{id}", createdMenuId.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);

        Map<String, Object> patchData = new HashMap<>();
        patchData.put("availableOnlyRestaurant", availableOnlyRestaurant);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(patchData, headers);

        updateMenuResponse = restTemplate.exchange(url, HttpMethod.PATCH, request, Map.class);
    }
}
