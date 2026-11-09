package com.gestioncomercial.gestion_comercial.contacto.application.port.out;

// para borrar un contacto hay que saber si aparece en alguna oportunidad
public interface OportunidadesDeContactoPort {
    boolean tieneOportunidades(Long contactoId);
}
