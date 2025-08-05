package stepdefinitions;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserStep {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    private ResponseEntity<Map> loginResponse;
    private ResponseEntity<Map> userResponse;
    private ResponseEntity<Map> usersListResponse;
    protected static String accessToken;
    private Map<String, Object> userData;
    private ResponseEntity<Map> userByIdResponse;
    private Integer createdUserId;
    private ResponseEntity<Map> updateResponse;
    private Map<String, Object> updatedUserData;
    private ResponseEntity<Void> passwordUpdateResponse;
    private ResponseEntity<Void> deleteResponse;

    @Dado("que eu tenho os dados do usuário admin Jack Ryan")
    public void que_eu_tenho_os_dados_do_usuário_admin_jack_ryan() {
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Rua das Flores");
        address.put("number", 123); // Número como inteiro
        address.put("city", "São Paulo");
        address.put("state", "SP");
        address.put("zipCode", "12345-678");

        userData = new HashMap<>();
        userData.put("name", "Jack Ryan");
        userData.put("email", "jackryan@restaurantsync.com");
        userData.put("login", "jackryan");
        userData.put("password", "password123");
        userData.put("address", address);
    }

    @Dado("que eu tenho os dados do usuário client John Doe")
    public void que_eu_tenho_os_dados_do_usuário_client_John_Doe() {
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Rua das Flores");
        address.put("number", 123);
        address.put("city", "São Paulo");
        address.put("state", "SP");
        address.put("zipCode", "12345-678");

        userData = new HashMap<>();
        userData.put("name", "John Doe");
        userData.put("email", "johndoe@example.com");
        userData.put("login", "johndoe");
        userData.put("password", "password123");
        userData.put("address", address);
    }

    @Quando("eu envio uma requisição POST para {string} com os dados do usuário")
    public void eu_envio_uma_requisição_post_para_com_os_dados_do_usuário(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        userResponse = restTemplate.postForEntity(endpoint, request, Map.class);
    }

    @Então("a resposta deve ter status {int}")
    public void a_resposta_deve_ter_status(Integer expectedStatus) {
        int actualStatus;
        if (passwordUpdateResponse != null) {
            actualStatus = passwordUpdateResponse.getStatusCodeValue();
        } else if (updateResponse != null) {
            actualStatus = updateResponse.getStatusCodeValue();
        } else if (userByIdResponse != null) {
            actualStatus = userByIdResponse.getStatusCodeValue();
        } else if (usersListResponse != null) {
            actualStatus = usersListResponse.getStatusCodeValue();
        } else if (deleteResponse != null) {
            actualStatus = deleteResponse.getStatusCodeValue();
        } else if (loginResponse != null) {
            actualStatus = loginResponse.getStatusCodeValue();
        } else {
            actualStatus = userResponse.getStatusCodeValue();
        }
        assertThat(actualStatus, equalTo(expectedStatus));
    }

    @Então("o corpo da resposta deve conter os dados do usuário criado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_usuário_criado() {
        int status = userResponse.getStatusCodeValue();

        if (status == 422) {
            // Se o usuário já existe, obtemos os dados via consulta
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "/v1/users?email=jackryan@restaurantsync.com",
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            assertThat(response.getStatusCodeValue(), equalTo(200));
            Map<String, Object> body = response.getBody();
            assertThat(body, notNullValue());

            List<Map<String, Object>> users = (List<Map<String, Object>>) body.get("content");
            assertThat(users, not(empty()));

            Map<String, Object> existingUser = users.get(0);
            assertThat(existingUser.get("name"), equalTo(userData.get("name")));
            assertThat(existingUser.get("email"), equalTo(userData.get("email")));
            assertThat(existingUser.get("login"), equalTo(userData.get("login")));
        } else {
            // Para status 201, verifica o corpo da resposta diretamente
            Map<String, Object> responseBody = userResponse.getBody();
            assertThat(responseBody, notNullValue());
            assertThat(responseBody.get("name"), equalTo(userData.get("name")));
            assertThat(responseBody.get("email"), equalTo(userData.get("email")));
            assertThat(responseBody.get("login"), equalTo(userData.get("login")));
        }
    }

    @Dado("que o usuário admin Jack Ryan está cadastrado")
    public void usuarioAdminEstaCadastrado() {
        que_eu_tenho_os_dados_do_usuário_admin_jack_ryan();
        eu_envio_uma_requisição_post_para_com_os_dados_do_usuário("/v1/users");

        int status = userResponse.getStatusCodeValue();

        // Em ambos os casos (201 ou 422), fazemos login para obter o token
        realizarLogin("jackryan@restaurantsync.com", "password123");
        assertThat(loginResponse.getStatusCodeValue(), equalTo(200));
        accessToken = (String) loginResponse.getBody().get("access_token");

        // Verificação adicional para garantir que o usuário existe
        if (status != 201 && status != 422) {
            fail("Falha ao verificar usuário admin. Status: " + status);
        }
    }

    @Então("a resposta deve ter status 201 ou 422 se usuário já existir")
    public void a_resposta_deve_ter_status_201_ou_422() {
        int actualStatus = userResponse.getStatusCodeValue();

        // Verifica apenas o status code - 201 ou 422 são aceitáveis
        assertThat("Status da resposta deve ser 201 (Created) ou 422 (Unprocessable Entity)",
                actualStatus, anyOf(equalTo(201), equalTo(422)));

        if (actualStatus == 422) {
            System.out.println("Usuário já existe (422), continuando o fluxo...");

            // Verifica se o corpo da resposta existe (opcional)
            if (userResponse.getBody() != null) {
                Map<String, Object> responseBody = userResponse.getBody();
                // Verifica se há mensagem de erro, mas não falha se não existir
                if (responseBody.containsKey("message")) {
                    System.out.println("Mensagem de erro: " + responseBody.get("message"));
                }
            }
        }
    }

    @Quando("eu realizo login com email {string} e senha {string}")
    public void realizarLogin(String email, String senha) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " +
                Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", email);
        body.add("password", senha);
        body.add("scope", "read write");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        loginResponse = restTemplate.postForEntity("/oauth2/token", entity, Map.class);

        assertThat(loginResponse.getStatusCodeValue(), equalTo(200));
        accessToken = (String) loginResponse.getBody().get("access_token");
        assertThat(accessToken, notNullValue());
    }

    @Então("o login deve ser bem sucedido")
    public void verificarLoginBemSucedido() {
        assertThat(loginResponse.getStatusCodeValue(), equalTo(200));
    }

    @Então("um token de acesso deve ser retornado")
    public void verificarTokenRetornado() {
        accessToken = (String) loginResponse.getBody().get("access_token");
        assertThat(accessToken, notNullValue());
        assertThat(accessToken, not(emptyString()));
    }

    @Quando("eu consulto a lista de usuários paginada em {string}")
    public void eu_consulto_a_lista_de_usuários_paginada_em(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        usersListResponse = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                request,
                Map.class
        );
    }

    @Então("a lista de usuários deve ser retornada com status {int}")
    public void a_lista_de_usuários_deve_ser_retornada_com_status(Integer expectedStatus) {
        assertThat(usersListResponse.getStatusCodeValue(), equalTo(expectedStatus));
    }

    @Então("o corpo da resposta deve conter a lista de usuários")
    public void o_corpo_da_resposta_deve_conter_a_lista_de_usuários() {
        Map<String, Object> responseBody = usersListResponse.getBody();
        assertThat(responseBody, notNullValue());

        // Verifica a estrutura básica de paginação
        assertThat(responseBody.containsKey("content"), is(true));
        assertThat(responseBody.get("content"), instanceOf(List.class));

        // Verifica os metadados de paginação (opcional)
        assertThat(responseBody.containsKey("pageable"), is(true));
        assertThat(responseBody.containsKey("totalElements"), is(true));
        assertThat(responseBody.containsKey("totalPages"), is(true));

        // Verifica se o usuário admin está na lista
        List<Map<String, Object>> users = (List<Map<String, Object>>) responseBody.get("content");

        for (Map<String, Object> user : users) {
            System.out.println("ID: " + user.get("id") +
                    " | Nome: " + user.get("name") +
                    " | Email: " + user.get("email"));

            // Se quiser ver o ID específico do admin
            if ("jackryan@restaurantsync.com".equals(user.get("email"))) {
                System.out.println("--> ADMIN ENCONTRADO! ID: " + user.get("id"));
                createdUserId = (Integer) user.get("id"); // Opcional: armazena o ID
            }
        }

        boolean found = users.stream().anyMatch(user ->
                "jackryan@restaurantsync.com".equals(user.get("email")));
        assertThat("Usuário admin não encontrado na lista", found, is(true));
    }

    @Quando("eu consulto o usuário com ID {int} em {string}")
    public void eu_consulto_o_usuário_com_ID_em(Integer userId, String endpoint) {
        // Verificação extra para garantir que temos um token válido
        if (accessToken == null || accessToken.isEmpty()) {
            realizarLogin("jackryan@restaurantsync.com", "password123");
            verificarLoginBemSucedido();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            userByIdResponse = restTemplate.exchange(
                    endpoint.replace("{id}", "1"), // Garante que o ID será 1
                    HttpMethod.GET,
                    request,
                    Map.class
            );
        } catch (Exception e) {
            System.err.println("Erro na consulta por ID: " + e.getMessage());
            throw e;
        }
    }

    @Então("o corpo da resposta deve conter os dados do usuário Jack Ryan")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_usuário_jack_ryan() {
        // Verifica primeiro o status da resposta
        assertThat(userByIdResponse.getStatusCodeValue(), equalTo(200));

        Map<String, Object> responseBody = userByIdResponse.getBody();

        // Verificações robustas
        assertThat("Resposta não pode ser nula", responseBody, notNullValue());
        assertThat("ID do usuário deve ser 1", responseBody.get("id"), equalTo(1));
        assertThat("Nome deve ser Jack Ryan", responseBody.get("name"), equalTo("Jack Ryan"));
        assertThat("Email deve corresponder", responseBody.get("email"), equalTo("jackryan@restaurantsync.com"));
        assertThat("Login deve ser jackryan", responseBody.get("login"), equalTo("jackryan"));

        // Verificação adicional do endereço (opcional)
        if (responseBody.containsKey("address")) {
            Map<String, Object> address = (Map<String, Object>) responseBody.get("address");
            assertThat("Rua deve ser Rua das Flores", address.get("street"), equalTo("Rua das Flores"));
        }
    }

    @Dado("eu tenho os dados atualizados do usuário para ID")
    public void eu_tenho_os_dados_atualizados_do_usuário_para_ID() {
        Map<String, Object> updatedAddress = new HashMap<>();
        updatedAddress.put("street", "Rua das Pedras");
        updatedAddress.put("number", 456);
        updatedAddress.put("city", "São Paulo");
        updatedAddress.put("state", "SP");
        updatedAddress.put("zipCode", "12345-896");

        updatedUserData = new HashMap<>();
        updatedUserData.put("name", "John Doe");
        updatedUserData.put("email", "john@example.com");
        updatedUserData.put("login", "johndoe");
        updatedUserData.put("password", "password123");
        updatedUserData.put("address", updatedAddress);
    }

    @Quando("eu envio uma requisição PUT para {string} com os dados do usuário")
    public void eu_envio_uma_requisição_put_para_com_os_dados_do_usuário(String endpoint) {
        if (accessToken == null || accessToken.isEmpty()) {
            realizarLogin("jackryan@restaurantsync.com", "password123");
            verificarLoginBemSucedido();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(updatedUserData, headers);

        String url = endpoint.replace("{id}", "2");
        System.out.println("PUT para: " + url);
        System.out.println("Corpo enviado: " + updatedUserData);

        updateResponse = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
    }

    @Então("o corpo da resposta deve conter os dados do usuário atualizado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_usuário_atualizado() {
        // Verifica primeiro o status da resposta
        assertThat(updateResponse.getStatusCodeValue(), equalTo(200));

        Map<String, Object> responseBody = updateResponse.getBody();
        assertThat("Resposta não pode ser nula", responseBody, notNullValue());

        // Verifica se a resposta contém os campos esperados
        assertThat("Nome deve ser John Doe", responseBody.get("name"), equalTo("John Doe"));
        assertThat("Email deve ser atualizado", responseBody.get("email"), equalTo("john@example.com"));

        // Verificação opcional do endereço
        if (responseBody.containsKey("address")) {
            Map<String, Object> address = (Map<String, Object>) responseBody.get("address");
            assertThat("Rua deve ser atualizada", address.get("street"), equalTo("Rua das Pedras"));
        }
    }

    @Quando("eu envio uma requisição PATCH para {string} com a nova senha {string}")
    public void eu_envio_requisicao_patch_para_com_nova_senha(String endpoint, String novaSenha) {
        if (accessToken == null || accessToken.isEmpty()) {
            realizarLogin("jack@restaurantsync.com", "password123");
            verificarLoginBemSucedido();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("password", novaSenha);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        passwordUpdateResponse = restTemplate.exchange(endpoint, HttpMethod.PATCH, request, Void.class);
    }

    @Quando("eu envio uma requisição DELETE para {string}")
    public void eu_envio_uma_requisição_DELETE_para(String endpoint) {
        if (accessToken == null || accessToken.isEmpty()) {
            realizarLogin("jackryan@restaurantsync.com", "password123");
            verificarLoginBemSucedido();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        deleteResponse = restTemplate.exchange(endpoint, HttpMethod.DELETE, request, Void.class);
    }
}
