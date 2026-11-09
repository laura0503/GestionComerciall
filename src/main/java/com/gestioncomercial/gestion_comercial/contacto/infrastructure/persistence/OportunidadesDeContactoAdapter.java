package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence.OportunidadJpaRepository;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.OportunidadesDeContactoPort;

// responde "¿aparece este contacto en alguna oportunidad?"
@Component
public class OportunidadesDeContactoAdapter implements OportunidadesDeContactoPort {

    private final OportunidadJpaRepository oportunidadRepository;

    public OportunidadesDeContactoAdapter(OportunidadJpaRepository oportunidadRepository) {
        this.oportunidadRepository = oportunidadRepository;
    }

    @Override
    public boolean tieneOportunidades(Long contactoId) {
        return oportunidadRepository.existsByContactoId(contactoId);
    }
}
