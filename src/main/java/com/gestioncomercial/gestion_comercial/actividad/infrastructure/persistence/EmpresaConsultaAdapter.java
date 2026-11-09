package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.gestioncomercial.gestion_comercial.EmpresaRepository;
import com.gestioncomercial.gestion_comercial.actividad.application.port.out.EmpresaConsultaPort;

// responde a "¿existe la empresa?" usando tu EmpresaRepository de siempre
@Component
public class EmpresaConsultaAdapter implements EmpresaConsultaPort {

    private final EmpresaRepository empresaRepository;

    public EmpresaConsultaAdapter(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public boolean existeEmpresa(Long empresaId) {
        return empresaRepository.existsById(empresaId); // existsById ya viene con JpaRepository
    }
}
