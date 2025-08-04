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
    @Então("a resposta de lista de restaurantes deve ter status {int}")
    public void a_resposta_deve_ter_status(int expectedStatus) {
        ResponseEntity<?> response = restaurantResponse != null
                ? restaurantResponse
                : restaurantsListResponse;
        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(expectedStatus));
    }

    @Então("o corpo da resposta deve conter a lista de restaurantes")
    public void o_corpo_da_resposta_deve_conter_a_lista_de_restaurantes() {
        Map<String, Object> body = restaurantsListResponse.getBody();
        assertThat(body, notNullValue());
        assertThat(body.containsKey("content"), is(true));
        assertThat(body.get("content"), instanceOf(List.class));
    }
}
