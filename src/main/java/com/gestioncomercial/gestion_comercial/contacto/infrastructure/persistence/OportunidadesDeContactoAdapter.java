package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.OportunidadRepository;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.OportunidadesDeContactoPort;

// responde "¿aparece este contacto en alguna oportunidad?"
@Component
public class OportunidadesDeContactoAdapter implements OportunidadesDeContactoPort {

    private final OportunidadRepository oportunidadRepository;

    public OportunidadesDeContactoAdapter(OportunidadRepository oportunidadRepository) {
        this.oportunidadRepository = oportunidadRepository;
    }

    @Override
    public boolean tieneOportunidades(Long contactoId) {
        return oportunidadRepository.existsByContactoId(contactoId);
    }
}
