package ru.vsu.cs.platon.docs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .description("API for managing users with JWT authorization")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))  // Добавляем замочек ко всем эндпоинтам
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")  // Указываем, что это JWT токен
                        ));
    }
}
