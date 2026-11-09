package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;

// traductor entre el dominio y la tabla
public class ActividadMapper {

    private ActividadMapper() {
        // constructor privado: solo tiene métodos static, no se crean objetos
    }

    // dominio → tabla (para guardar)
    public static ActividadEntity aEntity(Actividad a) {
        return new ActividadEntity(a.getId(), a.getTipo(), a.getAsunto(), a.getDescripcion(), a.getFecha(),
                a.isCompletada(), a.getEmpresaId());
    }

    // tabla → dominio (al leer)
    public static Actividad aDominio(ActividadEntity e) {
        return new Actividad(e.getId(), e.getTipo(), e.getAsunto(), e.getDescripcion(), e.getFecha(),
                e.isCompletada(), e.getEmpresaId());
    }
}
