package com.gestioncomercial.gestion_comercial.contacto.domain.model;

// modelo de dominio: Java puro. La empresa se guarda solo por su id (sin @ManyToOne)
public class Contacto {
    private final Long id;
    private String nombre;
    private String apellidos;
    private String cargo;
    private String email;
    private String telefono;
    private Long empresaId;

    // reconstruir un contacto que ya existe (lo usa el mapper al leer de la BD)
    public Contacto(Long id, String nombre, String apellidos, String cargo, String email, String telefono,
            Long empresaId) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.email = email;
        this.telefono = telefono;
        this.empresaId = empresaId;
    }

    // crear un contacto nuevo: valida y nace sin id
    public static Contacto nuevo(String nombre, String apellidos, String cargo, String email, String telefono,
            Long empresaId) {
        validar(nombre, email, empresaId);
        return new Contacto(null, nombre, apellidos, cargo, email, telefono, empresaId);
    }

    // cambiar los datos (también puede cambiar de empresa)
    public void actualizar(String nombre, String apellidos, String cargo, String email, String telefono,
            Long empresaId) {
        validar(nombre, email, empresaId);
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.email = email;
        this.telefono = telefono;
        this.empresaId = empresaId;
    }

    // ¿es de esta empresa? (lo necesitará Oportunidad)
    public boolean perteneceA(Long otraEmpresaId) {
        return empresaId.equals(otraEmpresaId);
    }

    private static void validar(String nombre, String email, Long empresaId) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del contacto es obligatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("La empresa del contacto es obligatoria");
        }
        if (email != null && !email.contains("@")) {
            throw new IllegalArgumentException("El email del contacto no es válido");
        }
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

    public Long getEmpresaId() {
        return empresaId;
    }
}
