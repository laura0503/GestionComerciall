package com.gestioncomercial.gestion_comercial.empresa.application.port.out;

// para borrar una empresa hay que saber si tiene cosas colgando.
// El núcleo pregunta solo eso, sin conocer los módulos de contactos u oportunidades
public interface DatosRelacionadosPort {
    boolean tieneContactos(Long empresaId);

    boolean tieneOportunidades(Long empresaId);
}
