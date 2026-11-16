package com.gestioncomercial.gestion_comercial.usuario.infrastructure.web.dto;

// lo que envía el cliente para iniciar sesión
public record LoginRequest(String email, String password) {
}
