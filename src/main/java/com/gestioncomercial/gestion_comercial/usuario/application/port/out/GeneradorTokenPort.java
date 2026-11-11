package com.gestioncomercial.gestion_comercial.usuario.application.port.out;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

// crear el token de sesión (detrás estará JWT)
public interface GeneradorTokenPort {
    String generar(Usuario usuario);
}
