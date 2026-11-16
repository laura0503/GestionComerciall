package com.gestioncomercial.gestion_comercial.usuario.infrastructure.persistence;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// la TABLA "usuarios". Aquí sí hay anotaciones JPA (estamos fuera del núcleo)
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // no puede haber dos usuarios con el mismo email
    private String email;

    @Column(name = "password_cifrada", nullable = false)
    private String passwordCifrada;

    @Enumerated(EnumType.STRING) // guarda "ADMIN", no 0
    @Column(nullable = false)
    private Rol rol;

    // JPA necesita un constructor vacío
    protected UsuarioEntity() {
    }

    public UsuarioEntity(Long id, String email, String passwordCifrada, Rol rol) {
        this.id = id;
        this.email = email;
        this.passwordCifrada = passwordCifrada;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordCifrada() {
        return passwordCifrada;
    }

    public Rol getRol() {
        return rol;
    }
}
