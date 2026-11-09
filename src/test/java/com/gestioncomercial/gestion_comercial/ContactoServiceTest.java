package com.gestioncomercial.gestion_comercial;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaJpaRepository;

import com.gestioncomercial.gestion_comercial.empresa.infrastructure.persistence.EmpresaEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.gestioncomercial.gestion_comercial.dto.ContactoRequest;
import com.gestioncomercial.gestion_comercial.dto.ContactoResponse;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;

// tests unitarios de ContactoService: los repositorios son falsos (mocks)
@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {

    @Mock
    private ContactoRepository contactoRepository;

    @Mock
    private EmpresaJpaRepository empresaRepository;

    @Mock
    private OportunidadRepository oportunidadRepository;

    @InjectMocks
    private ContactoService contactoService; // el de verdad, con los mocks dentro

    // empresa de prueba que usan varios tests
    private EmpresaEntity acme() {
        return new EmpresaEntity(1L, "Acme", "Tecnología", "Madrid", "Cliente");
    }

    @Test
    @DisplayName("Crear un contacto correcto lo guarda y devuelve el DTO")
    void crearContactoCorrecto() {
        // 1. Preparar
        ContactoRequest request = new ContactoRequest("Ana", "López", "Compras", "ana@acme.com", "600", 1L);
        // Cuando te pidan esto , responde con esto
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(acme()));
        when(contactoRepository.save(any(Contacto.class))).thenAnswer(invocacion -> {
            Contacto contacto = invocacion.getArgument(0); // el contacto que el Service quiere guardar
            contacto.setId(10L); // simulamos que la BD le pone el id
            return contacto;
        });

        // 2. Ejecutar
        ContactoResponse respuesta = contactoService.crear(request);

        // 3. Comprobar
        assertEquals(10L, respuesta.id());
        assertEquals("Ana", respuesta.nombre());
        assertEquals(1L, respuesta.empresaId());
        assertEquals("Acme", respuesta.empresaNombre());
    }

    @Test
    @DisplayName("Crear un contacto con una empresa que no existe lanza 404")
    void crearConEmpresaInexistente() {
        ContactoRequest request = new ContactoRequest("Ana", null, null, null, null, 99L);
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNoEncontradaException.class, () -> contactoService.crear(request));
        verify(contactoRepository, never()).save(any()); // no se ha guardado nada
    }

    @Test
    @DisplayName("Crear un contacto sin nombre lanza 400")
    void crearSinNombre() {
        ContactoRequest request = new ContactoRequest("", null, null, null, null, 1L);

        assertThrows(IllegalArgumentException.class, () -> contactoService.crear(request));
        verify(contactoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear un contacto con un email sin @ lanza 400")
    void crearConEmailInvalido() {
        ContactoRequest request = new ContactoRequest("Ana", null, null, "anaacme.com", null, 1L);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> contactoService.crear(request));
        assertEquals("El email del contacto no es válido", error.getMessage());
    }

    @Test
    @DisplayName("Borrar un contacto con oportunidades lanza 409 y no lo borra")
    void borrarConOportunidades() {
        Contacto ana = new Contacto(1L, "Ana", null, null, null, null, acme());
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(oportunidadRepository.existsByContactoId(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> contactoService.borrar(1L));
        verify(contactoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Borrar un contacto sin oportunidades lo borra")
    void borrarSinOportunidades() {
        Contacto ana = new Contacto(1L, "Ana", null, null, null, null, acme());
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(oportunidadRepository.existsByContactoId(1L)).thenReturn(false);

        contactoService.borrar(1L);

        verify(contactoRepository).deleteById(1L); // se ha llamado a borrar con el id 1
    }
}
