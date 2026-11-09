package com.gestioncomercial.gestion_comercial.contacto.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestioncomercial.gestion_comercial.contacto.application.port.out.ContactoRepositoryPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.EmpresaDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.OportunidadesDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.application.service.ContactoService;

// enchufa el núcleo de Contacto con sus adaptadores
@Configuration
public class ContactoConfig {

    @Bean
    public ContactoService contactoService(ContactoRepositoryPort contactoRepository,
            EmpresaDeContactoPort empresas, OportunidadesDeContactoPort oportunidades) {
        return new ContactoService(contactoRepository, empresas, oportunidades);
    }
}
