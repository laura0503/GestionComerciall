package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.ContactoDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.EmpresaDeOportunidadPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.OportunidadRepositoryPort;
import com.gestioncomercial.gestion_comercial.oportunidad.application.service.OportunidadService;

// enchufa el núcleo de Oportunidad con sus adaptadores
@Configuration
public class OportunidadConfig {

    @Bean
    public OportunidadService oportunidadService(OportunidadRepositoryPort oportunidadRepository,
            EmpresaDeOportunidadPort empresas, ContactoDeOportunidadPort contactos) {
        return new OportunidadService(oportunidadRepository, empresas, contactos);
    }
}
