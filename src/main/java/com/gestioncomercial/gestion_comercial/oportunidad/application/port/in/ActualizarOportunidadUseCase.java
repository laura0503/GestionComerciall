package com.gestioncomercial.gestion_comercial.oportunidad.application.port.in;

// caso de uso: cambiar todos los datos de una oportunidad
public interface ActualizarOportunidadUseCase {
    OportunidadDetalle actualizar(Long id, DatosOportunidad datos);
}
