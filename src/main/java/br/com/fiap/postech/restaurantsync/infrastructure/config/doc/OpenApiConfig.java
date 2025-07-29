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
                        .description("API for restaurant user management")
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

    @Bean
    public OpenApiCustomizer customizeApiResponses() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                // Ajusta o POST /v1/users
                if (path.equals("/v1/users")) {
                    pathItem.readOperations().forEach(operation -> {
                        if ("createUser".equals(operation.getOperationId())) {
                            operation.getResponses().remove("200");
                            if (!operation.getResponses().containsKey("201")) {
                                operation.getResponses().addApiResponse("201", new ApiResponse()
                                        .description("Usuário criado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/UserResponse"))
                                                )
                                        ));
                            }
                        }
                    });
                }

                // Ajusta o DELETE /v1/users/{id}
                if (path.equals("/v1/users/{id}")) {
                    pathItem.readOperations().forEach(operation -> {
                        if ("deleteUser".equals(operation.getOperationId())) {
                            operation.getResponses().clear();
                            operation.getResponses().addApiResponse("204", new ApiResponse()
                                    .description("Usuário removido com sucesso"));
                        }
                    });
                }

                // Ajusta o PATCH /v1/users/{id}/password
                if (path.equals("/v1/users/{id}/password")) {
                    pathItem.readOperations().forEach(operation -> {
                        if ("updatePassword".equals(operation.getOperationId())) {
                            operation.getResponses().clear();
                            operation.getResponses().addApiResponse("204", new ApiResponse()
                                    .description("Senha atualizada com sucesso"));
                        }
                    });
                }
            });
        };
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
                            .summary("Get OAuth2 Token")
                            .description("""
                                    Endpoint to obtain the access token via grant_type=password.
                                    
                                    **IMPORTANT:**  
                                    Before executing, click Authorize in the Swagger UI and enter your client_id as the username and client_secret as the password.
                                    
                                    Example curl call:
                                    curl -X POST 'http://localhost:8080/oauth2/token' \\
                                      -H 'Authorization: Basic <base64(client_id:client_secret)>' \\
                                      -H 'Content-Type: application/x-www-form-urlencoded' \\
                                      --data-urlencode 'username=johndoe@example.com' \\
                                      --data-urlencode 'password=securepassword' \\
                                      --data-urlencode 'grant_type=password'
                                    """)
                            .tags(java.util.List.of("Authentication"))
                            .security(java.util.List.of(new SecurityRequirement().addList("basicAuth")))
                            .requestBody(new RequestBody()
                                    .required(true)
                                    .content(new Content().addMediaType(
                                            "application/x-www-form-urlencoded",
                                            new MediaType().schema(requestBodySchema)
                                    ))
                            )
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse().description("Token generated successfully"))
                                    .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                    .addApiResponse("401", new ApiResponse().description("Unauthorized - make sure you clicked Authorize and filled in client_id/client_secret"))
                            )
            );

            openApi.path("/oauth2/token", tokenPath);
        };
    }
}