package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.contacto.application.port.out.EmpresaDeContactoPort;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

// responde "¿cómo se llama la empresa X?" (vacío si no existe)
@Component
public class EmpresaDeContactoAdapter implements EmpresaDeContactoPort {

    private final EmpresaJpaRepository empresaJpaRepository;

    public EmpresaDeContactoAdapter(EmpresaJpaRepository empresaJpaRepository) {
        this.empresaJpaRepository = empresaJpaRepository;
    }

    @Override
    public Optional<String> buscarNombre(Long empresaId) {
        return empresaJpaRepository.findById(empresaId).map(EmpresaEntity::getNombre);
    }
}
