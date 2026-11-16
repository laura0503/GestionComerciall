package com.gestioncomercial.gestion_comercial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.usuario.application.port.in.Sesion;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.CifradorPasswordPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.GeneradorTokenPort;
import com.gestioncomercial.gestion_comercial.usuario.application.port.out.UsuarioRepositoryPort;
import com.gestioncomercial.gestion_comercial.usuario.application.service.UsuarioService;
import com.gestioncomercial.gestion_comercial.usuario.domain.exception.CredencialesInvalidasException;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Rol;
import com.gestioncomercial.gestion_comercial.usuario.domain.model.Usuario;

// tests unitarios de usuarios: el servicio aislado, con los 3 PUERTOS mockeados
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private CifradorPasswordPort cifradorPasswordPort;

    @Mock
    private GeneradorTokenPort generadorTokenPort;

    @InjectMocks
    private UsuarioService usuarioService;

    // un usuario ya guardado en la BD, con la contraseña cifrada
    private Usuario ana() {
        return new Usuario(1L, "ana@email.com", "CIFRADA", Rol.COMERCIAL);
    }

    // simula que la BD guarda y devuelve el usuario con id 1
    private void guardarDevuelveConId() {
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            return new Usuario(1L, u.getEmail(), u.getPasswordCifrada(), u.getRol());
        });
    }

    // ---------- REGISTRAR ----------

    @Test
    @DisplayName("Registrar guarda la contraseña cifrada, el email en minúsculas y rol COMERCIAL por defecto")
    void registrarUsuario() {
        when(cifradorPasswordPort.cifrar("password123")).thenReturn("CIFRADA");
        when(usuarioRepositoryPort.existePorEmail("ana@email.com")).thenReturn(false);
        guardarDevuelveConId();

        Usuario resultado = usuarioService.registrar("Ana@Email.com", "password123", null);

        assertEquals("ana@email.com", resultado.getEmail()); // pasado a minúsculas
        assertEquals("CIFRADA", resultado.getPasswordCifrada()); // nunca la contraseña real
        assertEquals(Rol.COMERCIAL, resultado.getRol()); // rol por defecto
    }

    @Test
    @DisplayName("Una contraseña de menos de 8 caracteres lanza 400 y ni se cifra ni se guarda")
    void registrarConPasswordCorta() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar("ana@email.com", "1234", null));

        assertEquals("La contraseña debe tener al menos 8 caracteres", error.getMessage());
        verify(cifradorPasswordPort, never()).cifrar(anyString());
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar un email que ya existe lanza 409 y no guarda")
    void registrarEmailRepetido() {
        when(cifradorPasswordPort.cifrar("password123")).thenReturn("CIFRADA");
        when(usuarioRepositoryPort.existePorEmail("ana@email.com")).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class,
                () -> usuarioService.registrar("ana@email.com", "password123", null));

        assertEquals("Ya existe un usuario con el email ana@email.com", error.getMessage());
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    // ---------- INICIAR SESIÓN ----------

    @Test
    @DisplayName("Login correcto devuelve el token, el email y el rol")
    void loginCorrecto() {
        when(usuarioRepositoryPort.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(ana()));
        when(cifradorPasswordPort.coincide("password123", "CIFRADA")).thenReturn(true);
        when(generadorTokenPort.generar(any(Usuario.class))).thenReturn("TOKEN-JWT");

        Sesion sesion = usuarioService.iniciarSesion("ana@email.com", "password123");

        assertEquals("TOKEN-JWT", sesion.token());
        assertEquals("ana@email.com", sesion.email());
        assertEquals(Rol.COMERCIAL, sesion.rol());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza 401 y no genera token")
    void loginPasswordIncorrecta() {
        when(usuarioRepositoryPort.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(ana()));
        when(cifradorPasswordPort.coincide("otraPassword", "CIFRADA")).thenReturn(false);

        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> usuarioService.iniciarSesion("ana@email.com", "otraPassword"));

        assertEquals("Email o contraseña incorrectos", error.getMessage());
        verify(generadorTokenPort, never()).generar(any());
    }

    @Test
    @DisplayName("Login con un email que no existe da EL MISMO error que una contraseña incorrecta")
    void loginEmailInexistente() {
        when(usuarioRepositoryPort.buscarPorEmail("nadie@email.com")).thenReturn(Optional.empty());

        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> usuarioService.iniciarSesion("nadie@email.com", "password123"));

        assertEquals("Email o contraseña incorrectos", error.getMessage()); // no revela si el email existe
        verify(generadorTokenPort, never()).generar(any());
    }
}
