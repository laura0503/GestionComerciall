package com.gestioncomercial.gestion_comercial.actividad.domain.model;

import java.time.LocalDateTime;


import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadYaCompletadaException;

public class Actividad {
    private final Long id;
    private final TipoActividad tipo;
    private final String asunto;
    private final String descripcion;
    private final LocalDateTime fecha;
    private boolean completada;
    private final Long empresaId;

    public Actividad(Long id, TipoActividad tipo, String asunto, String descripcion, LocalDateTime fecha,
            boolean completada, Long empresaId) {
        this.id = id;
        this.tipo = tipo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.completada = completada;
        this.empresaId = empresaId;
    }

    public static Actividad nueva(TipoActividad tipo, String asunto, String descripcion, LocalDateTime fecha,
            Long empresaId) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de la actividad es obligatorio");
        }

        if (asunto == null || asunto.isBlank()) {
            throw new IllegalArgumentException("El asunto de la actividad es obligatorio");
        }

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la actividad es obligatoria");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("La empresa de la actividad es obligatoria");
        }

        return new Actividad(null, tipo, asunto, descripcion, fecha, false, empresaId);

    }

    public void completar() {
        if (completada) {
            throw new ActividadYaCompletadaException(id);
        }
        completada = true;
    }

    public boolean estaVencida(LocalDateTime ahora) {
        return !completada && fecha.isBefore(ahora);
    }

    public Long getId() {
        return id;
    }

    public TipoActividad getTipo() {
        return tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isCompletada() {
        return completada;
    }

    public Long getEmpresaId() {
        return empresaId;
    }
}
