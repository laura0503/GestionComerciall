package com.gestioncomercial.gestion_comercial;

import org.springframework.data.jpa.domain.Specification;

//root	La entidad -- > la entidad Empresas, como si fuera la tabla
//query	--> la consulta entera (el SELECT ... FROM ... WHERE ... ORDER BY); aquí no se usa
//cb	Una comparación	--> es la fábrica que crea las comparaciones
public class EmpresaSpecifications {
    public static Specification<Empresas> nombreContiene(String nombre) {
        return (root, query, cb) -> {
            if (nombre == null || nombre.isBlank()) {
                return cb.conjunction(); // condición siempre verdadera: no filtra
            }
            return cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
        };
        // "Contiene": % significa "cualquier cosa delante o detrás"

    }

    // WHERE sector = ?
    public static Specification<Empresas> conSector(String sector) {
        return (root, query, cb) -> {
            if (sector == null || sector.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("sector"), sector);

        };
    }

    // WHERE provincia = ?

    public static Specification<Empresas> conProvincia(String provincia) {
        return (root, query, cb) -> {
            if (provincia == null || provincia.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("provincia"), provincia);
        };
    }

    // WHERE estado = ?
    public static Specification<Empresas> conEstado(String estado) {
        return (root, query, cb) -> { // esto hace que JPA se cinvierta en SQL
            if (estado == null || estado.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("estado"), estado);
        };
        // root.get("estado") --> la columna estado de la entidad
    }

}
