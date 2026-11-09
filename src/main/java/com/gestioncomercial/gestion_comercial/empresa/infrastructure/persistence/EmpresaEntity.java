package com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// la TABLA de empresas (antes se llamaba Empresas y hacía también de modelo).
// @Table mantiene el nombre de siempre para no perder los datos de la BD
@Entity
@Table(name = "empresas")
public class EmpresaEntity {
    @Id // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre, sector, provincia, estado;

    public EmpresaEntity(Long id, String nombre, String sector, String provincia, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
        this.provincia = provincia;
        this.estado = estado;
    }

    // JPA necesita un constructor vacío
    public EmpresaEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
