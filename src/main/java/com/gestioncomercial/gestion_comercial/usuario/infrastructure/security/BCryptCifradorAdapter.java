package com.gestioncomercial.gestion_comercial.usuario.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.usuario.application.port.out.CifradorPasswordPort;

// quien RESPONDE al puerto de cifrado, usando BCrypt
@Component
public class BCryptCifradorAdapter implements CifradorPasswordPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String cifrar(String passwordPlana) {
        return encoder.encode(passwordPlana); // "password123" → "$2a$10$Xk3..."
    }

    @Override
    public boolean coincide(String passwordPlana, String passwordCifrada) {
        return encoder.matches(passwordPlana, passwordCifrada);
    }
}
