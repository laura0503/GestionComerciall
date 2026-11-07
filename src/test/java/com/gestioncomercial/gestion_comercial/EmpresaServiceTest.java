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

import com.gestioncomercial.gestion_comercial.dto.EmpresaRequest;
import com.gestioncomercial.gestion_comercial.dto.EmpresaResponse;
import com.gestioncomercial.gestion_comercial.exception.ConflictoException;
import com.gestioncomercial.gestion_comercial.exception.EmpresaNoEncontradaException;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {
    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ContactoRepository contactoRepository;

    @Mock
    private OportunidadRepository oportunidadRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private EmpresaRequest acEmpresaRequest() {
        return new EmpresaRequest("Acme", "Tecnología", "Madrid", "Cliente");

    }

    private Empresas acme() {
        return new Empresas(1L, "Acme", "Tecnología", "Madrid", "Cliente");
    }

    @Test
    @DisplayName("Crear una empresa correcta la guarda y deveuel el DTO")
    void crearEmpresaCorrecta() {
        when(empresaRepository.save(any(Empresas.class))).thenAnswer(invocacion -> {
            Empresas empresa = invocacion.getArgument(0);
            empresa.setId(1L);
            return empresa;

        });

        EmpresaResponse respuesta = empresaService.crear(acEmpresaRequest());

        assertEquals(1L, respuesta.id());
        assertEquals("Acme", respuesta.nombre());
        assertEquals("Madrid", respuesta.provincia());
    }

    @Test
    @DisplayName("Crear una empresa sin secto lanza 400 y no guarda")
    void crearSinSector() {
        EmpresaRequest request = new EmpresaRequest("Acme", "", "Madrid", "Cliente");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> empresaService.crear(request));
        assertEquals("El sector de la empresa es obligatorio", error.getMessage());
        verify(empresaRepository, never()).save(any());
        // any, para culquier lista
    }

    @Test
    @DisplayName("Crear un lote vacío lanza 400")
    void crearLoteVacio() {
        assertThrows(IllegalArgumentException.class, () -> empresaService.crearLote(List.of()));
        verify(empresaRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Si una empresa del lote no es válida, no se guarda ninguna")
    void crearLoteConUnaInvalida() {
        EmpresaRequest sinNombre = new EmpresaRequest("", "Hostelería", "Sevilla", "Potencial");
        List<EmpresaRequest> lote = List.of(acEmpresaRequest(), sinNombre); // la segunda está mal

        assertThrows(IllegalArgumentException.class, () -> empresaService.crearLote(lote));
        verify(empresaRepository, never()).saveAll(anyList()); // ni siquiera la primera
    }

    @Test
    @DisplayName("Buscar una empresa que no existe lanza 404")
    void buscarPorIdInexistente() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmpresaNoEncontradaException.class, () -> empresaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Borrar una empresa con contactos lanza 409 y no la borra")
    void borrarConContactos() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(acme()));
        when(contactoRepository.existsByEmpresaId(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> empresaService.borrar(1L));
        verify(empresaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Borrar una empresa sin contactos ni oportunidades la borra")
    void borrarSinDatosRelacionados() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(acme()));
        when(contactoRepository.existsByEmpresaId(1L)).thenReturn(false);
        when(oportunidadRepository.existsByEmpresaId(1L)).thenReturn(false);

        empresaService.borrar(1L);

        verify(empresaRepository).deleteById(1L);
    }

    /*
     * acmeRequest() y acme() Uno crea el DTO (lo que envía el cliente) y otro la
     * entidad (lo que hay en la BD). Así no repites datos en cada test
     */
}
