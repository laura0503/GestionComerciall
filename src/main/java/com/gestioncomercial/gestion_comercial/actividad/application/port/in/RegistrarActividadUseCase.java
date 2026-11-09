package com.gestioncomercial.gestion_comercial.actividad.application.port.in;

import java.time.LocalDateTime;

import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

// caso de uso: dar de alta una actividad nueva
public interface RegistrarActividadUseCase {
    Actividad registrar(TipoActividad tipo, String asunto, String descripcion, LocalDateTime fecha, Long empresaId);
}
