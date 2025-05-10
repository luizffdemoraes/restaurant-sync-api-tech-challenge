package br.com.fiap.postech.restaurantsync.config.doc;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                );
    }
}
