package com.gestioncomercial.gestion_comercial;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity // tabla OPORTUNIDAD
public class Oportunidad {
    @Id // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @Column(precision = 12, scale = 2) // hasta 12 cifras, 2 de ellas decimales
    private BigDecimal importe;

    @Enumerated(EnumType.STRING) // guarda el texto ("PROPUESTA"), no un número
    private Fase fase;

    private LocalDate fechaCierrePrevista;

    @ManyToOne // muchas oportunidades pertenecen a una empresa
    @JoinColumn(name = "empresa_id", nullable = false) // clave foránea obligatoria
    private Empresas empresa;

    @ManyToOne // contacto principal de la oportunidad
    @JoinColumn(name = "contacto_id") // opcional: puede ser null
    private Contacto contacto;

    public Oportunidad(Long id, String titulo, BigDecimal importe, Fase fase, LocalDate fechaCierrePrevista,
            Empresas empresa, Contacto contacto) {
        this.id = id;
        this.titulo = titulo;
        this.importe = importe;
        this.fase = fase;
        this.fechaCierrePrevista = fechaCierrePrevista;
        this.empresa = empresa;
        this.contacto = contacto;
    }

    public Oportunidad() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Fase getFase() {
        return fase;
    }

    public void setFase(Fase fase) {
        this.fase = fase;
    }

    public LocalDate getFechaCierrePrevista() {
        return fechaCierrePrevista;
    }

    public void setFechaCierrePrevista(LocalDate fechaCierrePrevista) {
        this.fechaCierrePrevista = fechaCierrePrevista;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }
}
