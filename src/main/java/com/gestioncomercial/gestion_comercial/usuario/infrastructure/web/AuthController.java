package com.gestioncomercial.gestion_comercial.usuario.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gestioncomercial.gestion_comercial.usuario.application.port.in.IniciarSesionUseCase;
import com.gestioncomercial.gestion_comercial.usuario.application.port.in.RegistrarUsuarioUseCase;
import com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto.LoginRequest;
import com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto.LoginResponse;
import com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto.RegistroRequest;
import com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto.UsuarioResponse;

// adaptador de ENTRADA: traduce HTTP ⇄ casos de uso de usuarios
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // depende de los puertos IN (interfaces), no de UsuarioService
    private final RegistrarUsuarioUseCase registrarUseCase;
    private final IniciarSesionUseCase iniciarSesionUseCase;

    public AuthController(RegistrarUsuarioUseCase registrarUseCase, IniciarSesionUseCase iniciarSesionUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.iniciarSesionUseCase = iniciarSesionUseCase;
    }

    // POST /api/auth/registro → 201
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody RegistroRequest request) {
        return UsuarioResponse.desde(registrarUseCase.registrar(request.email(), request.password(), request.rol()));
    }

    // POST /api/auth/login → 200 con el token
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return LoginResponse.desde(iniciarSesionUseCase.iniciarSesion(request.email(), request.password()));
    }
}
