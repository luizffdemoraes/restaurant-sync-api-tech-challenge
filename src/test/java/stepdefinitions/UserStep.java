package stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserStep extends StepDefsDefault {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private Map<String, String> userData = new HashMap<>();

    @Dado("que eu tenho os seguintes dados do usuário admin:")
    public void queEuTenhoOsSeguintesDadosDoUsuarioAdmin(List<Map<String, String>> data) {
        data.forEach(row -> userData.put(row.get("campo"), row.get("valor")));
    }

    @Dado("que eu tenho os seguintes dados do usuário cliente:")
    public void queEuTenhoOsSeguintesDadosDoUsuarioCliente(List<Map<String, String>> data) {
        data.forEach(row -> userData.put(row.get("campo"), row.get("valor")));
    }

    @Quando("eu envio uma requisição POST para {string} com os dados do usuário")
    public void euEnvioUmaRequisicaoPOSTParaComOsDadosDoUsuario(String uri) throws Exception {
        // Verificar se todos os campos obrigatórios estão presentes
        if (!userData.containsKey("number") || userData.get("number") == null) {
            throw new IllegalArgumentException("O campo 'number' é obrigatório");
        }

        int number;
        try {
            number = Integer.parseInt(userData.get("number").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo 'number' deve ser um número válido: " + userData.get("number"));
        }

        // Construir objeto de endereço
        Map<String, Object> address = new HashMap<>();
        address.put("street", userData.get("street"));
        address.put("number", number);
        address.put("city", userData.get("city"));
        address.put("state", userData.get("state"));
        address.put("zipCode", userData.get("zipCode"));

        // Construir objeto principal
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", userData.get("name"));
        requestBody.put("email", userData.get("email"));
        requestBody.put("login", userData.get("login"));
        requestBody.put("password", userData.get("password"));
        requestBody.put("address", address);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        response = restTemplate.postForEntity(uri, entity, String.class);
    }

    @Então("a resposta deve ter status {int}")
    public void aRespostaDeveTerStatus(Integer status) {
        assertThat(response.getStatusCodeValue(), equalTo(status));
    }

    @Então("o corpo da resposta deve conter os dados do usuário criado")
    public void oCorpoDaRespostaDeveConterOsDadosDoUsuárioCriado() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resp = mapper.readValue(response.getBody(), Map.class);

        assertThat(resp.get("id"), notNullValue());
        assertThat(resp.get("name"), equalTo(userData.get("name")));
        assertThat(resp.get("email"), equalTo(userData.get("email")));
        assertThat(resp.get("login"), equalTo(userData.get("login")));

        // Verificar endereço
        Map<String, Object> respAddress = (Map<String, Object>) resp.get("address");
        assertThat(respAddress.get("street"), equalTo(userData.get("street")));
        assertThat(respAddress.get("city"), equalTo(userData.get("city")));
        assertThat(respAddress.get("state"), equalTo(userData.get("state")));
        assertThat(respAddress.get("zipCode"), equalTo(userData.get("zipCode")));
    }
}