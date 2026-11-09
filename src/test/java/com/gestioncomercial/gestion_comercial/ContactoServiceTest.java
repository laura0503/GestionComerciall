package com.gestioncomercial.gestion_comercial;

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

import com.gestioncomercial.gestion_comercial.contacto.application.port.in.ContactoDetalle;
import com.gestioncomercial.gestion_comercial.contacto.application.port.in.DatosContacto;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.ContactoRepositoryPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.EmpresaDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.application.port.out.OportunidadesDeContactoPort;
import com.gestioncomercial.gestion_comercial.contacto.application.service.ContactoService;
import com.gestioncomercial.gestion_comercial.contacto.domain.model.Contacto;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;

// tests unitarios de ContactoService: los PUERTOS son falsos (mocks)
@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {

    @Mock
    private ContactoRepositoryPort contactoRepository;

    @Mock
    private EmpresaDeContactoPort empresas;

    @Mock
    private OportunidadesDeContactoPort oportunidades;

    @InjectMocks
    private ContactoService contactoService; // el de verdad, con los mocks dentro

    // contacto de prueba (empresa 1) que usan varios tests
    private Contacto ana() {
        return new Contacto(1L, "Ana", null, null, null, null, 1L);
    }

    @Test
    @DisplayName("Crear un contacto correcto lo guarda y lo devuelve con el nombre de la empresa")
    void crearContactoCorrecto() {
        // 1. Preparar
        DatosContacto datos = new DatosContacto("Ana", "López", "Compras", "ana@acme.com", "600", 1L);
        when(empresas.buscarNombre(1L)).thenReturn(Optional.of("Acme"));
        when(contactoRepository.guardar(any(Contacto.class))).thenAnswer(invocacion -> {
            Contacto c = invocacion.getArgument(0); // el contacto que el service quiere guardar
            // simulamos que la BD le pone el id 10
            return new Contacto(10L, c.getNombre(), c.getApellidos(), c.getCargo(), c.getEmail(),
                    c.getTelefono(), c.getEmpresaId());
        });

        // 2. Ejecutar
        ContactoDetalle resultado = contactoService.crear(datos);

        // 3. Comprobar
        assertEquals(10L, resultado.contacto().getId());
        assertEquals("Ana", resultado.contacto().getNombre());
        assertEquals(1L, resultado.contacto().getEmpresaId());
        assertEquals("Acme", resultado.empresaNombre());
    }

    @Test
    @DisplayName("Crear un contacto con una empresa que no existe lanza 404")
    void crearConEmpresaInexistente() {
        DatosContacto datos = new DatosContacto("Ana", null, null, null, null, 99L);
        when(empresas.buscarNombre(99L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNoEncontradaException.class, () -> contactoService.crear(datos));
        verify(contactoRepository, never()).guardar(any()); // no se ha guardado nada
    }

    @Test
    @DisplayName("Crear un contacto sin nombre lanza 400")
    void crearSinNombre() {
        DatosContacto datos = new DatosContacto("", null, null, null, null, 1L);

        assertThrows(IllegalArgumentException.class, () -> contactoService.crear(datos));
        verify(contactoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear un contacto con un email sin @ lanza 400")
    void crearConEmailInvalido() {
        DatosContacto datos = new DatosContacto("Ana", null, null, "anaacme.com", null, 1L);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> contactoService.crear(datos));
        assertEquals("El email del contacto no es válido", error.getMessage());
    }

    @Test
    @DisplayName("Borrar un contacto con oportunidades lanza 409 y no lo borra")
    void borrarConOportunidades() {
        when(contactoRepository.buscarPorId(1L)).thenReturn(Optional.of(ana()));
        when(oportunidades.tieneOportunidades(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> contactoService.borrar(1L));
        verify(contactoRepository, never()).borrar(any());
    }

    @Test
    @DisplayName("Borrar un contacto sin oportunidades lo borra")
    void borrarSinOportunidades() {
        when(contactoRepository.buscarPorId(1L)).thenReturn(Optional.of(ana()));
        when(oportunidades.tieneOportunidades(1L)).thenReturn(false);

        contactoService.borrar(1L);

        verify(contactoRepository).borrar(1L); // se ha llamado a borrar con el id 1
    }
}
