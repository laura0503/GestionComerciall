package com.gestioncomercial.gestion_comercial.empresa.application.port.in;

// los datos que llegan para crear o actualizar una empresa (sin id)
public record DatosEmpresa(String nombre, String sector, String provincia, String estado) {
}
