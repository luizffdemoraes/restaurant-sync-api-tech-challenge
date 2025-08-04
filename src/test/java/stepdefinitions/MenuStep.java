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
    private ResponseEntity<Map> menuResponse;
    private ResponseEntity<Map> menusListResponse;


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
        ResponseEntity<?> response = menusListResponse != null ? menusListResponse : menuResponse;
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
}
