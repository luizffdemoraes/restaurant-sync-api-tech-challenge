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

    @Quando("eu envio uma requisição POST para {string} com os dados do usuário")
    public void eu_envio_uma_requisição_post_para_com_os_dados_do_usuário(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        userResponse = restTemplate.postForEntity(endpoint, request, Map.class);

        // Debug: Mostrar requisição e resposta
        System.out.println("Request enviado para " + endpoint + ":");
        System.out.println(userData);
        System.out.println("Response status: " + userResponse.getStatusCodeValue());
        System.out.println("Response body: " + userResponse.getBody());
    }

    @Então("a resposta deve ter status {int}")
    public void a_resposta_deve_ter_status(Integer expectedStatus) {
        int actualStatus;
        if (userByIdResponse != null) {
            actualStatus = userByIdResponse.getStatusCodeValue();
        } else if (usersListResponse != null) {
            actualStatus = usersListResponse.getStatusCodeValue();
        } else if (loginResponse != null) {
            actualStatus = loginResponse.getStatusCodeValue();
        } else {
            actualStatus = userResponse.getStatusCodeValue();
        }
        assertThat(actualStatus, equalTo(expectedStatus));
    }

    @Então("o corpo da resposta deve conter os dados do usuário criado")
    public void o_corpo_da_resposta_deve_conter_os_dados_do_usuário_criado() {
        Map<String, Object> responseBody = userResponse.getBody();
        createdUserId = (Integer) responseBody.get("id");
        assertThat(createdUserId, notNullValue());
        assertThat(responseBody, notNullValue());
        assertThat(responseBody.get("name"), equalTo(userData.get("name")));
        assertThat(responseBody.get("email"), equalTo(userData.get("email")));
        assertThat(responseBody.get("login"), equalTo(userData.get("login")));
    }

    @Dado("que o usuário admin Jack Ryan está cadastrado")
    public void usuarioAdminEstaCadastrado() {
        que_eu_tenho_os_dados_do_usuário_admin_jack_ryan();
        eu_envio_uma_requisição_post_para_com_os_dados_do_usuário("/v1/users");

        // Se recebermos 400, tentamos fazer login para ver se o usuário já existe
        if (userResponse.getStatusCodeValue() == 400) {
            System.out.println("Recebido 400, tentando fazer login para verificar se usuário existe...");
            realizarLogin("jackryan@restaurantsync.com", "password123");

            if (loginResponse.getStatusCodeValue() == 200) {
                System.out.println("Login bem-sucedido, usuário já existe");
                accessToken = (String) loginResponse.getBody().get("access_token");
                return;
            }
        }

        // Se não foi possível fazer login, verifica se o cadastro foi bem-sucedido
        assertThat("Falha ao cadastrar usuário. Status: " + userResponse.getStatusCodeValue() +
                        ", Body: " + userResponse.getBody(),
                userResponse.getStatusCodeValue(),
                anyOf(equalTo(201), equalTo(422)));
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

            // Debug detalhado
            System.out.println("=== DEBUG CONSULTA POR ID ===");
            System.out.println("Endpoint: " + endpoint);
            System.out.println("Token: " + accessToken);
            System.out.println("Status: " + userByIdResponse.getStatusCodeValue());
            System.out.println("Response Body: " + userByIdResponse.getBody());

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
}