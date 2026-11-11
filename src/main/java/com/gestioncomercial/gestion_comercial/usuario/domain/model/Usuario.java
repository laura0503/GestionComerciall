package com.gestioncomercial.gestion_comercial.usuario.domain.model;

public class Usuario {
    private final Long id;
    private final String email;
    private final String passwordCifrada; // NUNCA la contraseña en texto plano
    private final Rol rol;

    public Usuario(Long id, String email, String passwordCifrada, Rol rol) {
        this.id = id;
        this.email = email;
        this.passwordCifrada = passwordCifrada;
        this.rol = rol;
    }

    // crear un usuario nuevo: recibe la contraseña YA cifrada
    public static Usuario nuevo(String email, String passwordCifrada, Rol rol) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        if (passwordCifrada == null || passwordCifrada.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        // si no se indica rol, por defecto es COMERCIAL (el de menos permisos)
        Rol rolFinal = (rol == null) ? Rol.COMERCIAL : rol;
        return new Usuario(null, email.trim().toLowerCase(), passwordCifrada, rolFinal);
    }

    // regla de negocio sobre la contraseña ANTES de cifrarla
    public static void validarPassword(String passwordPlana) {
        if (passwordPlana == null || passwordPlana.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
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
