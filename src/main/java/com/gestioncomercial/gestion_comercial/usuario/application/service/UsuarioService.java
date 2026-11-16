package com.gestioncomercial.gestion_comercial.usuario.application.service;

import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.usuario.application.port.in.IniciarSesionUseCase;
import com.gestioncomercial.gestion_comercial.usuario.application.port.in.RegistrarUsuarioUseCase;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.GeneradorTokenPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.UsuarioRepositoryPort;
import com.gestioncomercial.gestion_comercial.usuario.domain.exception.CredencialesInvalidasException;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.CifradorPasswordPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.in.Sesion;

//Incluye los 2 casos de uso  tanto out como in
// SIN @Service: el núcleo no conoce Spring (se registra en UsuarioConfig)
public class UsuarioService implements RegistrarUsuarioUseCase, IniciarSesionUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final CifradorPasswordPort cifradorPasswordPort;
    private final GeneradorTokenPort generadorToken;

    public UsuarioService(UsuarioRepositoryPort usuarioRepository, CifradorPasswordPort cifrador,
            GeneradorTokenPort generadorToken) {
        this.usuarioRepositoryPort = usuarioRepository;
        this.cifradorPasswordPort = cifrador;
        this.generadorToken = generadorToken;
    }

    @Override
    public Usuario registrar(String email, String password, Rol rol) {

        Usuario.validarPassword(password);

        Usuario usuario = Usuario.nuevo(email, cifradorPasswordPort.cifrar(password), rol);

        if (usuarioRepositoryPort.existePorEmail(usuario.getEmail())) {
            throw new ConflictoException("Ya existe un usuario con el email " + usuario.getEmail());
        }

        return usuarioRepositoryPort.guardar(usuario);
    }

    @Override
    public Sesion iniciarSesion(String email, String password) {

        if (email == null || password == null) {
            throw new CredencialesInvalidasException();
        }

        // busca al usuario y compeurba la contraseña y si uno de los dso falals salta
        // exception
        Usuario usuario = usuarioRepositoryPort.buscarPorEmail(email.trim().toLowerCase())
                .filter(u -> cifradorPasswordPort.coincide(password, u.getPasswordCifrada()))
                .orElseThrow(CredencialesInvalidasException::new);

        String token = generadorToken.generar(usuario);

        return new Sesion(token, usuario.getEmail(), usuario.getRol());
    }

}
