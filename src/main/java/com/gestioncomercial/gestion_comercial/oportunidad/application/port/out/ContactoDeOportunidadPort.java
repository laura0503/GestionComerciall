package com.gestioncomercial.gestion_comercial.oportunidad.application.port.out;

import java.util.Optional;

// datos básicos de un contacto (vacío si no existe)
public interface ContactoDeOportunidadPort {
    Optional<ContactoResumen> buscar(Long contactoId);
}
