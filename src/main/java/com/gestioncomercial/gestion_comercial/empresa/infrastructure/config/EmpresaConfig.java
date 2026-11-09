package com.gestioncomercial.gestion_comercial.empresa.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestioncomercial.gestion_comercial.empresa.application.port.out.DatosRelacionadosPort;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.EmpresaRepositoryPort;
import com.gestioncomercial.gestion_comercial.empresa.application.service.EmpresaService;

// enchufa el núcleo de Empresa con sus adaptadores
@Configuration
public class EmpresaConfig {

    @Bean
    public EmpresaService empresaService(EmpresaRepositoryPort empresaRepository,
            DatosRelacionadosPort datosRelacionados) {
        return new EmpresaService(empresaRepository, datosRelacionados);
    }
}
