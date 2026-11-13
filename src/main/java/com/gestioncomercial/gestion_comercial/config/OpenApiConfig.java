package com.gestioncomercial.gestion_comercial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// datos que muestra la cabecera de Swagger + el botón "Authorize" para el token
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("GestionComercial-LA")
                        .version("1.0")
                        .description("API REST de un CRM: empresas, contactos, oportunidades y actividades"))

                // 1. le dice a Swagger QUÉ tipo de seguridad usa la API: un token JWT en la cabecera
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))

                // 2. le dice que lo envíe en TODAS las peticiones
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
