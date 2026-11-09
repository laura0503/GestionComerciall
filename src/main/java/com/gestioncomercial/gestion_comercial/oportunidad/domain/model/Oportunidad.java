package com.gestioncomercial.gestion_comercial.oportunidad.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// modelo de dominio: Java puro. Empresa y contacto solo por su id (sin @ManyToOne)
public class Oportunidad {
    private final Long id;
    private String titulo;
    private BigDecimal importe;
    private Fase fase;
    private LocalDate fechaCierrePrevista;
    private Long empresaId;
    private Long contactoId; // opcional: puede ser null

    // reconstruir una oportunidad que ya existe (lo usa el mapper)
    public Oportunidad(Long id, String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
            Long empresaId, Long contactoId) {
        this.id = id;
        this.titulo = titulo;
        this.importe = importe;
        this.fase = fase;
        this.fechaCierrePrevista = fechaCierrePrevista;
        this.empresaId = empresaId;
        this.contactoId = contactoId;
    }

    // crear una oportunidad nueva: valida y, si no traen fase, empieza en PROSPECCION
    public static Oportunidad nueva(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
            Long empresaId, Long contactoId) {
        validar(titulo, importe, empresaId);
        Fase faseInicial = fase != null ? fase : Fase.PROSPECCION; // arriba del embudo
        return new Oportunidad(null, titulo, importe, faseInicial, fechaCierrePrevista, empresaId, contactoId);
    }

    // cambiar los datos. Si no envían fase, se queda la que tenía
    public void actualizar(String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
            Long empresaId, Long contactoId) {
        validar(titulo, importe, empresaId);
        this.titulo = titulo;
        this.importe = importe;
        if (fase != null) {
            this.fase = fase;
        }
        this.fechaCierrePrevista = fechaCierrePrevista;
        this.empresaId = empresaId;
        this.contactoId = contactoId;
    }

    // mover de fase sin tocar el resto
    public void cambiarFase(Fase nuevaFase) {
        if (nuevaFase == null) {
            throw new IllegalArgumentException("La fase es obligatoria");
        }
        this.fase = nuevaFase;
    }

    private static void validar(String titulo, BigDecimal importe, Long empresaId) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la oportunidad es obligatorio");
        }
        if (importe == null) {
            throw new IllegalArgumentException("El importe de la oportunidad es obligatorio");
        }
        if (importe.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("La empresa de la oportunidad es obligatoria");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public Fase getFase() {
        return fase;
    }

    public LocalDate getFechaCierrePrevista() {
        return fechaCierrePrevista;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public Long getContactoId() {
        return contactoId;
    }
}
