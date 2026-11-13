package com.gestioncomercial.gestion_comercial.usuario.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gestioncomercial.gestion_comercial.usuario.application.port.out.CifradorPasswordPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.GeneradorTokenPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.UsuarioRepositoryPort;
import com.gestioncomercial.gestion_comercial.usuario.application.service.UsuarioService;

// aquí se "enchufa" todo: Spring crea el service pasándole los 3 adaptadores
@Configuration
public class UsuarioConfig {

    @Bean
    public UsuarioService usuarioService(UsuarioRepositoryPort usuarioRepository, CifradorPasswordPort cifrador,
            GeneradorTokenPort generadorToken) {
        return new UsuarioService(usuarioRepository, cifrador, generadorToken);
    }
}
