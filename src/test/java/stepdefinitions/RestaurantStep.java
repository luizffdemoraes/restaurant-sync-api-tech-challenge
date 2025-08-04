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

public class RestaurantStep {

    @Autowired
    private TestRestTemplate restTemplate;

    private Map<String, Object> restaurantData;
    private ResponseEntity<Map> restaurantResponse;
    private ResponseEntity<Map> restaurantsListResponse;
    private ResponseEntity<Map> restaurantByIdResponse;
    private Map<String, Object> updateData;
    private ResponseEntity<Map> updateResponse;
    private ResponseEntity<Void> deleteResponse;
    public static Integer createdRestaurantId;

    @Dado("eu tenho os dados do restaurante {string}")
    public void que_eu_tenho_os_dados_do_restaurante(String name) {
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Avenida Paulista");
        address.put("number", 1000);
        address.put("city", "São Paulo");
        address.put("state", "SP");
        address.put("zipCode", "01310-100");

        restaurantData = new HashMap<>();
        restaurantData.put("name", name);
        restaurantData.put("address", address);
        restaurantData.put("cuisineType", "French");
        restaurantData.put("openingHours", "10:00-22:00");
        restaurantData.put("ownerId", 1);
    }

    @Quando("eu envio uma requisição POST para {string} com os dados do restaurante")
    public void eu_envio_uma_requisição_POST_para_com_os_dados_do_restaurante(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(restaurantData, headers);
        restaurantResponse = restTemplate.postForEntity(endpoint, request, Map.class);
    }

    @Então("o corpo da resposta deve conter os dados do restaurante criado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_restaurante_criado() {
        Map<String, Object> body = restaurantResponse.getBody();
        assertThat(body.get("id"), notNullValue());
        assertThat(body.get("name"), equalTo(restaurantData.get("name")));

        Map<String, Object> respAddress = (Map<String, Object>) body.get("address");
        Map<String, Object> reqAddress = (Map<String, Object>) restaurantData.get("address");
        assertThat(respAddress.get("street"), equalTo(reqAddress.get("street")));
        assertThat(respAddress.get("number"), equalTo(reqAddress.get("number")));
        assertThat(respAddress.get("city"), equalTo(reqAddress.get("city")));
        assertThat(respAddress.get("state"), equalTo(reqAddress.get("state")));
        assertThat(respAddress.get("zipCode"), equalTo(reqAddress.get("zipCode")));

        assertThat(body.get("cuisineType"), equalTo(restaurantData.get("cuisineType")));
        assertThat(body.get("openingHours"), equalTo(restaurantData.get("openingHours")));
        assertThat(body.get("ownerId"), equalTo(restaurantData.get("ownerId")));
    }


    @Quando("eu consulto a lista de restaurantes em {string}")
    public void eu_consulto_lista_de_restaurantes_em(String endpoint) {
        if (UserStep.accessToken == null || UserStep.accessToken.isEmpty()) {
            new UserStep().realizarLogin("jackryan@restaurantsync.com", "password123");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        restaurantsListResponse = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                request,
                Map.class
        );
    }

    @Então("a resposta do restaurante deve ter status {int}")
    public void a_resposta_do_restaurante_deve_ter_status(int expectedStatus) {
        ResponseEntity<?> response =
                deleteResponse != null ? deleteResponse :
                        updateResponse != null ? updateResponse :
                                restaurantsListResponse != null ? restaurantsListResponse :
                                        restaurantByIdResponse != null ? restaurantByIdResponse :
                                                restaurantResponse;

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(expectedStatus));
    }

    @Então("a resposta de deleção de restaurante deve ter status {int}")
    public void a_resposta_de_delecao_de_restaurante_deve_ter_status(int expectedStatus) {
        assertThat(deleteResponse, notNullValue());
        assertThat(deleteResponse.getStatusCodeValue(), equalTo(expectedStatus));
    }

    @Então("o corpo da resposta deve conter a lista de restaurantes")
    public void o_corpo_da_resposta_deve_conter_a_lista_de_restaurantes() {
        Map<String, Object> body = restaurantsListResponse.getBody();
        assertThat(body, notNullValue());
        assertThat(body.containsKey("content"), is(true));
        assertThat(body.get("content"), instanceOf(List.class));
    }

    @Quando("eu consulto o restaurante com ID {int} em {string}")
    public void consultar_restaurante_por_id(int id, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String url = endpoint.replace("{id}", String.valueOf(id));
        restaurantByIdResponse = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    }

    @Então("o corpo da resposta deve conter os dados do restaurante consultado")
    public void validar_corpo_restaurante_consultado() {
        Map<String, Object> body = restaurantByIdResponse.getBody();
        assertThat(body.get("id"), equalTo(1));
        assertThat(body.get("name"), notNullValue());
        assertThat(body.containsKey("address"), is(true));
        assertThat(body.get("address"), instanceOf(Map.class));
        assertThat(body.get("cuisineType"), notNullValue());
        assertThat(body.get("openingHours"), notNullValue());
        assertThat(body.get("ownerId"), notNullValue());
    }

    @Dado("eu tenho os dados atualizados do restaurante")
    public void eu_tenho_os_dados_atualizados_do_restaurante() {
        updateData = new HashMap<>();
        updateData.put("name", "Gourmet Bistro JP");
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Avenida Paulista 2");
        address.put("number", 1001);
        address.put("city", "São Paulo 2");
        address.put("state", "SP2");
        address.put("zipCode", "01310-101");
        updateData.put("address", address);
        updateData.put("cuisineType", "Brazil");
        updateData.put("openingHours", "10:00-22:00");
        updateData.put("ownerId", 1);
    }

    @Quando("eu envio uma requisição PUT para {string} com os dados atualizados")
    public void eu_envio_requisicao_PUT_para_com_dados_atualizados(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateData, headers);
        String url = endpoint.replace("{id}", "1");
        updateResponse = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
    }


    @Então("o corpo da resposta deve conter os dados do restaurante atualizado")
    public void validar_corpo_restaurante_atualizado() {
        Map<String, Object> body = updateResponse.getBody();
        assertThat(body.get("id"), equalTo(1));
        assertThat(body.get("name"), equalTo(updateData.get("name")));

        Map<String, Object> respAddress = (Map<String, Object>) body.get("address");
        Map<String, Object> reqAddress = (Map<String, Object>) updateData.get("address");
        assertThat(respAddress.get("street"), equalTo(reqAddress.get("street")));
        assertThat(respAddress.get("number"), equalTo(reqAddress.get("number")));
        assertThat(respAddress.get("city"), equalTo(reqAddress.get("city")));
        assertThat(respAddress.get("state"), equalTo(reqAddress.get("state")));
        assertThat(respAddress.get("zipCode"), equalTo(reqAddress.get("zipCode")));

        assertThat(body.get("cuisineType"), equalTo(updateData.get("cuisineType")));
        assertThat(body.get("openingHours"), equalTo(updateData.get("openingHours")));
        assertThat(body.get("ownerId"), equalTo(updateData.get("ownerId")));
    }

    @Quando("eu envio uma requisição DELETE restaurante para {string}")
    public void eu_envio_uma_requisicao_DELETE_restaurante_para(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);
        String url = endpoint.replace("{id}", "2");
        deleteResponse = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
    }


    @Dado("o restaurante {string} está cadastrado")
    public void o_restaurante_esta_cadastrado(String name) {
        que_eu_tenho_os_dados_do_restaurante(name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + UserStep.accessToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(restaurantData, headers);
        restaurantResponse = restTemplate.postForEntity("/v1/restaurants", request, Map.class);

        assertThat(restaurantResponse, notNullValue());
        assertThat(restaurantResponse.getStatusCodeValue(), equalTo(201));

        Map<String, Object> body = restaurantResponse.getBody();
        assertThat(body.get("id"), notNullValue());
        assertThat(body.get("name"), equalTo(restaurantData.get("name")));

        createdRestaurantId = (Integer) body.get("id");
    }
}
