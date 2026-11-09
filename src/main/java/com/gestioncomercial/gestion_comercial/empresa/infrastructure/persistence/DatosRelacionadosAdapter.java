package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.ContactoRepository;
import com.gestioncomercial.gestion_comercial.OportunidadRepository;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.DatosRelacionadosPort;

// responde a "¿tiene contactos / oportunidades?" usando los repositorios de esos módulos.
// Es el ÚNICO sitio del módulo Empresa que conoce Contacto y Oportunidad
@Component
public class DatosRelacionadosAdapter implements DatosRelacionadosPort {

    private final ContactoRepository contactoRepository;
    private final OportunidadRepository oportunidadRepository;

    public DatosRelacionadosAdapter(ContactoRepository contactoRepository,
            OportunidadRepository oportunidadRepository) {
        this.contactoRepository = contactoRepository;
        this.oportunidadRepository = oportunidadRepository;
    }

    @Override
    public boolean tieneContactos(Long empresaId) {
        return contactoRepository.existsByEmpresaId(empresaId);
    }

    @Override
    public boolean tieneOportunidades(Long empresaId) {
        return oportunidadRepository.existsByEmpresaId(empresaId);
    }
}
