package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Oportunidad;

// traductor entre el dominio (ids) y la tabla (@ManyToOne)
public class OportunidadMapper {

    private OportunidadMapper() {
    }

    // dominio → tabla. Empresa y contacto llegan ya preparados desde el adaptador
    public static OportunidadEntity aEntity(Oportunidad o, EmpresaEntity empresa, ContactoEntity contacto) {
        return new OportunidadEntity(o.getId(), o.getTitulo(), o.getImporte(), o.getFase(),
                o.getFechaCierrePrevista(), empresa, contacto);
    }

    // tabla → dominio: de los objetos solo nos quedamos con los ids
    public static Oportunidad aDominio(OportunidadEntity e) {
        Long contactoId = e.getContacto() != null ? e.getContacto().getId() : null; // el contacto es opcional
        return new Oportunidad(e.getId(), e.getTitulo(), e.getImporte(), e.getFase(), e.getFechaCierrePrevista(),
                e.getEmpresa().getId(), contactoId);
    }
}
