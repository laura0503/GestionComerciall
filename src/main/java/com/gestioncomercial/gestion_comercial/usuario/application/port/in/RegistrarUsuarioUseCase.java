package com.gestioncomercial.gestion_comercial.usuario.application.port.in;

import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

public interface RegistrarUsuarioUseCase {
    Usuario registrar(String email, String password, Rol rol);

}
