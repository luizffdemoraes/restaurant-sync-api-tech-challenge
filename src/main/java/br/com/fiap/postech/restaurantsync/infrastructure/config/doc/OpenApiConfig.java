package br.com.fiap.postech.restaurantsync.infrastructure.config.doc;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Sync API")
                        .version("1.0.0")
                        .description("API para gerenciamento de usuários de restaurantes")
                        .contact(new Contact()
                                .name("Luiz Moraes")
                                .email("lffm1994@gmail.com")
                                .url("https://localhost:8080"))
                )
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                        )
                );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public OpenApiCustomizer oauth2TokenEndpointCustomiser() {
        return openApi -> {

            Map<String, Schema<Object>> properties = new HashMap<>();
            properties.put("grant_type", new Schema<>().type("string").example("password"));
            properties.put("username", new Schema<>().type("string").example("johndoe@example.com"));
            properties.put("password", new Schema<>().type("string").example("password123"));

            Schema<Object> requestBodySchema = new Schema<>();
            requestBodySchema.setType("object");
            requestBodySchema.setProperties((Map) properties);

            PathItem tokenPath = new PathItem().post(
                    new Operation()
                            .summary("Obter token OAuth2")
                            .description("""
                                    Endpoint para obter o token de acesso via grant_type=password.

                                    **IMPORTANTE:**  
                                    Antes de executar, clique em **Authorize** no Swagger UI e informe seu `client_id` como usuário e `client_secret` como senha.

                                    Exemplo de chamada cURL:
                                    curl -X POST 'http://localhost:8080/oauth2/token' \\
                                      -H 'Authorization: Basic <base64(client_id:client_secret)>' \\
                                      -H 'Content-Type: application/x-www-form-urlencoded' \\
                                      --data-urlencode 'username=johndoe@example.com' \\
                                      --data-urlencode 'password=securepassword' \\
                                      --data-urlencode 'grant_type=password'
                                    """)
                            .tags(java.util.List.of("Autenticação"))
                            .security(java.util.List.of(new SecurityRequirement().addList("basicAuth")))
                            .requestBody(new RequestBody()
                                    .required(true)
                                    .content(new Content().addMediaType(
                                            "application/x-www-form-urlencoded",
                                            new MediaType().schema(requestBodySchema)
                                    ))
                            )
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse().description("Token gerado com sucesso"))
                                    .addApiResponse("400", new ApiResponse().description("Requisição inválida"))
                                    .addApiResponse("401", new ApiResponse().description("Não autorizado - verifique se clicou em Authorize e preencheu client_id/client_secret"))
                            )
            );

            openApi.path("/oauth2/token", tokenPath);
        };
    }
}