package com.gestioncomercial.gestion_comercial.usuario.application.port.out;

// cifrar y comprobar contraseñas (detrás estará BCrypt)
public interface CifradorPasswordPort {
    // "miPassword123" → "$2a$10$Xk3..." (no se puede volver atrás)
    String cifrar(String passwordPlana);

    // ¿esta contraseña escrita en el login corresponde a la cifrada guardada?
    boolean coincide(String passwordPlana, String passwordCifrada);
}
