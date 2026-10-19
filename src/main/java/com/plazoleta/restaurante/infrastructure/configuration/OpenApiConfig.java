package com.plazoleta.restaurante.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
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
        final String esquemaSeguridad = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Plazoleta de Comidas - Microservicio Restaurante")
                        .description("API para la gestión de restaurantes, platos y empleados")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(esquemaSeguridad,
                                new SecurityScheme()
                                        .name(esquemaSeguridad)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(esquemaSeguridad));
    }
}