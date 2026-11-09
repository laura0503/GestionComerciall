package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

// caso de uso: dar de alta una oportunidad de venta
public interface CrearOportunidadUseCase {
    OportunidadDetalle crear(DatosOportunidad datos);
}
