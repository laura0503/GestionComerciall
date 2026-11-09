package com.gestioncomercial.gestion_comercial.actividad.application.port.out;

// para registrar una actividad hay que comprobar que la empresa existe.
// En vez de usar EmpresaRepository directamente (eso nos ataría al otro módulo),
// pedimos solo lo justo: "¿existe esta empresa, sí o no?"
public interface EmpresaConsultaPort {

    boolean existeEmpresa(Long empresaId);
}
