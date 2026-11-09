package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;
import com.gestioncomercial.gestion_comercial.oportunidad.application.port.out.EmpresaDeOportunidadPort;

// responde "¿cómo se llama la empresa X?" (vacío si no existe)
@Component
public class EmpresaDeOportunidadAdapter implements EmpresaDeOportunidadPort {

    private final EmpresaJpaRepository empresaJpaRepository;

    public EmpresaDeOportunidadAdapter(EmpresaJpaRepository empresaJpaRepository) {
        this.empresaJpaRepository = empresaJpaRepository;
    }

    @Override
    public Optional<String> buscarNombre(Long empresaId) {
        return empresaJpaRepository.findById(empresaId).map(EmpresaEntity::getNombre);
    }
}
