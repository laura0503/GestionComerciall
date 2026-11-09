package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.actividad.application.port.out.EmpresaConsultaPort;

// responde a "¿existe la empresa?" usando tu EmpresaRepository de siempre
@Component
public class EmpresaConsultaAdapter implements EmpresaConsultaPort {

    private final EmpresaJpaRepository empresaRepository;

    public EmpresaConsultaAdapter(EmpresaJpaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public boolean existeEmpresa(Long empresaId) {
        return empresaRepository.existsById(empresaId); // existsById ya viene con JpaRepository
    }
}
