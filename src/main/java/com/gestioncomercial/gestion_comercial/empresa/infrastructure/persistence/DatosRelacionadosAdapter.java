package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.OportunidadRepository;
import com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence.ActividadJpaRepository;
import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoJpaRepository;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.DatosRelacionadosPort;

// responde a "¿tiene contactos / oportunidades / actividades?" usando los repositorios de esos módulos.
// Es el ÚNICO sitio del módulo Empresa que conoce los otros módulos
@Component
public class DatosRelacionadosAdapter implements DatosRelacionadosPort {

    private final ContactoJpaRepository contactoRepository;
    private final OportunidadRepository oportunidadRepository;
    private final ActividadJpaRepository actividadRepository;

    public DatosRelacionadosAdapter(ContactoJpaRepository contactoRepository,
            OportunidadRepository oportunidadRepository, ActividadJpaRepository actividadRepository) {
        this.contactoRepository = contactoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.actividadRepository = actividadRepository;
    }

    @Override
    public boolean tieneContactos(Long empresaId) {
        return contactoRepository.existsByEmpresaId(empresaId);
    }

    @Override
    public boolean tieneOportunidades(Long empresaId) {
        return oportunidadRepository.existsByEmpresaId(empresaId);
    }

    @Override
    public boolean tieneActividades(Long empresaId) {
        return actividadRepository.existsByEmpresaId(empresaId);
    }
}
