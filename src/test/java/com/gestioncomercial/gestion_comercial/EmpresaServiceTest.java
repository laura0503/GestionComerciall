package com.gestioncomercial.gestion_comercial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestioncomercial.gestion_comercial.empresa.application.port.in.DatosEmpresa;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.DatosRelacionadosPort;
import com.gestioncomercial.gestion_comercial.empresa.application.port.out.EmpresaRepositoryPort;
import com.gestioncomercial.gestion_comercial.empresa.application.service.EmpresaService;
import com.gestioncomercial.gestion_comercial.empresa.domain.exception.EmpresaNoEncontradaException;
import com.gestioncomercial.gestion_comercial.empresa.domain.model.Empresa;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;

// los mismos 7 casos de antes, pero ahora se mockean los PUERTOS (interfaces), no los repositorios JPA
@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {
    @Mock
    private EmpresaRepositoryPort empresaRepository;

    @Mock
    private DatosRelacionadosPort datosRelacionados;

    @InjectMocks
    private EmpresaService empresaService;

    private DatosEmpresa datosAcme() {
        return new DatosEmpresa("Acme", "Tecnología", "Madrid", "Cliente");
    }

    private Empresa acme() {
        return new Empresa(1L, "Acme", "Tecnología", "Madrid", "Cliente");
    }

    @Test
    @DisplayName("Crear una empresa correcta la guarda y la devuelve con id")
    void crearEmpresaCorrecta() {
        // la "BD" devuelve la empresa con id 1 (Empresa no tiene setId: se crea otra con el id)
        when(empresaRepository.guardar(any(Empresa.class))).thenAnswer(invocacion -> {
            Empresa e = invocacion.getArgument(0);
            return new Empresa(1L, e.getNombre(), e.getSector(), e.getProvincia(), e.getEstado());
        });

        Empresa resultado = empresaService.crear(datosAcme());

        assertEquals(1L, resultado.getId());
        assertEquals("Acme", resultado.getNombre());
        assertEquals("Madrid", resultado.getProvincia());
    }

    @Test
    @DisplayName("Crear una empresa sin sector lanza 400 y no guarda")
    void crearSinSector() {
        DatosEmpresa datos = new DatosEmpresa("Acme", "", "Madrid", "Cliente");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> empresaService.crear(datos));
        assertEquals("El sector de la empresa es obligatorio", error.getMessage());
        verify(empresaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear un lote vacío lanza 400")
    void crearLoteVacio() {
        assertThrows(IllegalArgumentException.class, () -> empresaService.crearLote(List.of()));
        verify(empresaRepository, never()).guardarTodas(anyList());
    }

    @Test
    @DisplayName("Si una empresa del lote no es válida, no se guarda ninguna")
    void crearLoteConUnaInvalida() {
        DatosEmpresa sinNombre = new DatosEmpresa("", "Hostelería", "Sevilla", "Potencial");
        List<DatosEmpresa> lote = List.of(datosAcme(), sinNombre); // la segunda está mal

        assertThrows(IllegalArgumentException.class, () -> empresaService.crearLote(lote));
        verify(empresaRepository, never()).guardarTodas(anyList()); // ni siquiera la primera
    }

    @Test
    @DisplayName("Buscar una empresa que no existe lanza 404")
    void buscarPorIdInexistente() {
        when(empresaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNoEncontradaException.class, () -> empresaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Borrar una empresa con contactos lanza 409 y no la borra")
    void borrarConContactos() {
        when(empresaRepository.buscarPorId(1L)).thenReturn(Optional.of(acme()));
        when(datosRelacionados.tieneContactos(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> empresaService.borrar(1L));
        verify(empresaRepository, never()).borrar(any());
    }

    @Test
    @DisplayName("Borrar una empresa con actividades lanza 409 y no la borra")
    void borrarConActividades() {
        when(empresaRepository.buscarPorId(1L)).thenReturn(Optional.of(acme()));
        when(datosRelacionados.tieneContactos(1L)).thenReturn(false);
        when(datosRelacionados.tieneOportunidades(1L)).thenReturn(false);
        when(datosRelacionados.tieneActividades(1L)).thenReturn(true);

        ConflictoException error = assertThrows(ConflictoException.class, () -> empresaService.borrar(1L));
        assertEquals("No se puede borrar la empresa 1 porque tiene actividades", error.getMessage());
        verify(empresaRepository, never()).borrar(any());
    }

    @Test
    @DisplayName("Borrar una empresa sin contactos, oportunidades ni actividades la borra")
    void borrarSinDatosRelacionados() {
        when(empresaRepository.buscarPorId(1L)).thenReturn(Optional.of(acme()));
        when(datosRelacionados.tieneContactos(1L)).thenReturn(false);
        when(datosRelacionados.tieneOportunidades(1L)).thenReturn(false);
        when(datosRelacionados.tieneActividades(1L)).thenReturn(false);

        empresaService.borrar(1L);

        verify(empresaRepository).borrar(1L);
    }
}
