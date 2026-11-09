package com.gestioncomercial.gestion_comercial.empresa.domain.model;

// modelo de dominio: Java puro, sin JPA ni Spring
public class Empresa {
    private final Long id;
    private String nombre;
    private String sector;
    private String provincia;
    private String estado;

    // reconstruir una empresa que ya existe (la usa el mapper al leer de la BD)
    public Empresa(Long id, String nombre, String sector, String provincia, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
        this.provincia = provincia;
        this.estado = estado;
    }

    // crear una empresa nueva: valida y nace sin id (lo pondrá la BD)
    public static Empresa nueva(String nombre, String sector, String provincia, String estado) {
        validar(nombre, sector, provincia, estado);
        return new Empresa(null, nombre, sector, provincia, estado);
    }

    // cambiar los datos de una empresa existente (el id no cambia)
    public void actualizar(String nombre, String sector, String provincia, String estado) {
        validar(nombre, sector, provincia, estado);
        this.nombre = nombre;
        this.sector = sector;
        this.provincia = provincia;
        this.estado = estado;
    }

    // las mismas reglas que tenía EmpresaService, ahora en el dominio
    private static void validar(String nombre, String sector, String provincia, String estado) {
        if (estaVacio(nombre)) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio");
        }
        if (estaVacio(sector)) {
            throw new IllegalArgumentException("El sector de la empresa es obligatorio");
        }
        if (estaVacio(provincia)) {
            throw new IllegalArgumentException("La provincia de la empresa es obligatoria");
        }
        if (estaVacio(estado)) {
            throw new IllegalArgumentException("El estado de la empresa es obligatorio");
        }
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSector() {
        return sector;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getEstado() {
        return estado;
    }
}
