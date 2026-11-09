package com.gestioncomercial.gestion_comercial.oportunidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.gestioncomercial.gestion_comercial.contacto.infrastructure.persistence.ContactoEntity;
import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;
import com.gestioncomercial.gestion_comercial.oportunidad.domain.model.Fase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// la TABLA de oportunidades. Los @ManyToOne se quedan aquí (claves foráneas en la BD)
@Entity
@Table(name = "oportunidad") // el nombre de siempre, para no perder datos
public class OportunidadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @Column(precision = 12, scale = 2) // hasta 12 cifras, 2 de ellas decimales
    private BigDecimal importe;

    @Enumerated(EnumType.STRING) // guarda el texto ("PROPUESTA"), no un número
    private Fase fase;

    private LocalDate fechaCierrePrevista;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false) // clave foránea obligatoria
    private EmpresaEntity empresa;

    @ManyToOne
    @JoinColumn(name = "contacto_id") // opcional: puede ser null
    private ContactoEntity contacto;

    public OportunidadEntity(Long id, String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
            EmpresaEntity empresa, ContactoEntity contacto) {
        this.id = id;
        this.titulo = titulo;
        this.importe = importe;
        this.fase = fase;
        this.fechaCierrePrevista = fechaCierrePrevista;
        this.empresa = empresa;
        this.contacto = contacto;
    }

    // JPA necesita un constructor vacío
    public OportunidadEntity() {
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

    public EmpresaEntity getEmpresa() {
        return empresa;
    }

    public ContactoEntity getContacto() {
        return contacto;
    }
}
