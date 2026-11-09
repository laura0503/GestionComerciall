package com.gestioncomercial.gestion_comercial.actividad.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestioncomercial.gestion_comercial.actividad.application.port.out.ActividadRepositoryPort;
import com.gestioncomercial.gestion_comercial.actividad.application.port.out.EmpresaConsultaPort;
import com.gestioncomercial.gestion_comercial.actividad.application.service.ActividadService;

// aquí se "enchufa" todo: Spring crea el service pasándole los adaptadores
@Configuration
public class ActividadConfig {

    @Bean
    public ActividadService actividadService(ActividadRepositoryPort actividadRepository,
            EmpresaConsultaPort empresaConsulta) {
        return new ActividadService(actividadRepository, empresaConsulta);
    }
}
