package com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// la TABLA de contactos (antes se llamaba Contacto y hacía también de modelo).
// El @ManyToOne se queda AQUÍ, en infraestructura: así la BD mantiene la clave foránea
@Entity
@Table(name = "contacto") // el nombre de siempre, para no perder datos
public class ContactoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre, apellidos, cargo, email, telefono;

    @ManyToOne
    @JoinColumn(name = "empresas_id", nullable = false) // clave foránea
    private EmpresaEntity empresa;

    public ContactoEntity(Long id, String nombre, String apellidos, String cargo, String email, String telefono,
            EmpresaEntity empresa) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.email = email;
        this.telefono = telefono;
        this.empresa = empresa;
    }

    // JPA necesita un constructor vacío
    public ContactoEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCargo() {
        return cargo;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public EmpresaEntity getEmpresa() {
        return empresa;
    }
}
