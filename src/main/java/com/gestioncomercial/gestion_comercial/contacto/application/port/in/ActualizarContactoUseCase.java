package com.gestioncomercial.gestion_comercial.contacto.application.port.in;

// caso de uso: cambiar los datos de un contacto
public interface ActualizarContactoUseCase {
    ContactoDetalle actualizar(Long id, DatosContacto datos);
}
