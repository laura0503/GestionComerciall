package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;

// traductor entre el dominio y la tabla.
// Aquí es donde "empresaId" (dominio) ⇄ "@ManyToOne EmpresaEntity" (tabla)
public class ContactoMapper {

    private ContactoMapper() {
    }

    // dominio → tabla. La empresa llega ya preparada desde el adaptador
    public static ContactoEntity aEntity(Contacto contacto, EmpresaEntity empresa) {
        return new ContactoEntity(contacto.getId(), contacto.getNombre(), contacto.getApellidos(),
                contacto.getCargo(), contacto.getEmail(), contacto.getTelefono(), empresa);
    }

    // tabla → dominio: del objeto empresa solo nos quedamos con su id
    public static Contacto aDominio(ContactoEntity entity) {
        return new Contacto(entity.getId(), entity.getNombre(), entity.getApellidos(), entity.getCargo(),
                entity.getEmail(), entity.getTelefono(), entity.getEmpresa().getId());
    }
}
