package com.gestioncomercial.gestion_comercial.contacto.application.port.in;

// caso de uso: dar de alta un contacto
public interface CrearContactoUseCase {
    ContactoDetalle crear(DatosContacto datos);
}
