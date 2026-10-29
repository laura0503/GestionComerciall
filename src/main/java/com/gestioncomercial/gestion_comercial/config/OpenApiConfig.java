package com.gestioncomercial.gestion_comercial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

// datos que muestra la cabecera de Swagger
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("GestionComercial-LA")
                        .version("1.0")
                        .description("API REST de un CRM: empresas, contactos y oportunidades de venta"));
    }
}
