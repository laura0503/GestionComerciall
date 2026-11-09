package com.gestioncomercial.gestion_comercial.actividad.infrastructure.persistence;

import java.time.LocalDateTime;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// la TABLA de la BD. Aquí sí hay anotaciones JPA (estamos fuera del núcleo)
@Entity
@Table(name = "actividades")
public class ActividadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // guarda "LLAMADA", no 0
    @Column(nullable = false)
    private TipoActividad tipo;

    @Column(nullable = false)
    private String asunto;

    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private boolean completada;

    // solo el id, sin @ManyToOne: el módulo no se ata a la entidad Empresas
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    // JPA necesita un constructor vacío
    protected ActividadEntity() {
    }

    public ActividadEntity(Long id, TipoActividad tipo, String asunto, String descripcion, LocalDateTime fecha,
            boolean completada, Long empresaId) {
        this.id = id;
        this.tipo = tipo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.completada = completada;
        this.empresaId = empresaId;
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
