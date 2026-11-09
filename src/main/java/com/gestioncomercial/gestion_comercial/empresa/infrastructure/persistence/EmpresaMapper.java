package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;

// traductor entre el dominio y la tabla
public class EmpresaMapper {

    private EmpresaMapper() {
        // solo métodos static
    }

    // dominio → tabla (para guardar)
    public static EmpresaEntity aEntity(Empresa empresa) {
        return new EmpresaEntity(empresa.getId(), empresa.getNombre(), empresa.getSector(),
                empresa.getProvincia(), empresa.getEstado());
    }

    // tabla → dominio (al leer)
    public static Empresa aDominio(EmpresaEntity entity) {
        return new Empresa(entity.getId(), entity.getNombre(), entity.getSector(),
                entity.getProvincia(), entity.getEstado());
    }
}
