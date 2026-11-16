package com.gestioncomercial.gestion_comercial.usuario.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.usuario.application.port.in.RegistrarUsuarioUseCase;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.UsuarioRepositoryPort;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;

// al ARRANCAR la app, crea el primer ADMIN (si no existe ya) con los datos del .env
@Component
public class AdminInicial implements ApplicationRunner {
    private final RegistrarUsuarioUseCase registrarUseCase;
    private final String email, password;
    private final UsuarioRepositoryPort usuarioRepository;

    public AdminInicial(RegistrarUsuarioUseCase registrarUseCase, UsuarioRepositoryPort usuarioRepository,
            @Value("${admin.email:}") String email, @Value("${admin.password:}") String password) {
        this.email = email;
        this.password = password;
        this.usuarioRepository = usuarioRepository;
        this.registrarUseCase = registrarUseCase;
    }

    // Spring ejecuta este método UNA vez, justo después de arrancar
    @Override
    public void run(ApplicationArguments args) {
        // si no hay datos en el .env (por ejemplo, en los tests), no hace nada
        if (email.isBlank() || password.isBlank()) {
            return;
        }
        // si ya existe (segundo arranque, tercero...), no lo vuelve a crear
        if (usuarioRepository.existePorEmail(email.trim().toLowerCase())) {
            return;
        }
        // usa el MISMO caso de uso que el registro: valida, cifra y guarda
        registrarUseCase.registrar(email, password, Rol.ADMIN);
    }
}
