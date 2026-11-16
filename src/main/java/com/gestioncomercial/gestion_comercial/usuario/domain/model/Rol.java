package com.gestioncomercial.gestion_comercial.usuario.domain.model;

// qué puede hacer cada usuario
public enum Rol {
    ADMIN, // puede todo, incluido borrar
    COMERCIAL // trabaja con empresas, contactos, oportunidades y actividades, pero no borra
}
