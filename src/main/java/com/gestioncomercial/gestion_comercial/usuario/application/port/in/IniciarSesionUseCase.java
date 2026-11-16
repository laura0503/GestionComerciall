package com.gestioncomercial.gestion_comercial.usuario.application.port.in;

public interface IniciarSesionUseCase {
    Sesion iniciarSesion(String email, String password);
}
