package com.gestioncomercial.gestion_comercial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestioncomercial.gestion_comercial.actividad.application.port.out.ActividadRepositoryPort;
import com.gestioncomercial.gestion_comercial.actividad.application.port.out.EmpresaConsultaPort;
import com.gestioncomercial.gestion_comercial.actividad.application.service.ActividadService;
import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadNoEncontradaException;
import com.gestioncomercial.gestion_comercial.actividad.domain.exception.ActividadYaCompletadaException;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.Actividad;
import com.gestioncomercial.gestion_comercial.actividad.domain.model.TipoActividad;

// tests unitarios de actividades: el servicio aislado, con los PUERTOS mockeados
@ExtendWith(MockitoExtension.class)
class ActividadServiceTest {

    @Mock
    private ActividadRepositoryPort actividadRepository;

    @Mock
    private EmpresaConsultaPort empresaConsulta;

    @InjectMocks
    private ActividadService actividadService;

    private static final LocalDateTime MANANA = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime AYER = LocalDateTime.now().minusDays(1);

    // una llamada de la empresa 1 con la fecha y el estado que necesite cada test
    private Actividad llamada(Long id, LocalDateTime fecha, boolean completada) {
        return new Actividad(id, TipoActividad.LLAMADA, "Llamar a Ana", null, fecha, completada, 1L);
    }

    // simula que la BD guarda y devuelve la actividad con id 1
    private void guardarDevuelveConId() {
        when(actividadRepository.guardar(any(Actividad.class))).thenAnswer(invocacion -> {
            Actividad a = invocacion.getArgument(0);
            return new Actividad(1L, a.getTipo(), a.getAsunto(), a.getDescripcion(), a.getFecha(),
                    a.isCompletada(), a.getEmpresaId());
        });
    }

    @Test
    @DisplayName("Una actividad nueva se guarda como pendiente")
    void registrarActividad() {
        when(empresaConsulta.existeEmpresa(1L)).thenReturn(true);
        guardarDevuelveConId();

        Actividad resultado = actividadService.registrar(TipoActividad.LLAMADA, "Llamar a Ana", null, MANANA, 1L);

        assertEquals(1L, resultado.getId());
        assertFalse(resultado.isCompletada()); // empieza sin completar
    }

    @Test
    @DisplayName("Registrar en una empresa que no existe lanza 400 y no guarda")
    void registrarEnEmpresaInexistente() {
        when(empresaConsulta.existeEmpresa(99L)).thenReturn(false);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> actividadService.registrar(TipoActividad.LLAMADA, "Llamar a Ana", null, MANANA, 99L));
        assertEquals("La empresa 99 no existe", error.getMessage());
        verify(actividadRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar sin asunto lanza 400 y ni siquiera consulta la empresa")
    void registrarSinAsunto() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> actividadService.registrar(TipoActividad.LLAMADA, "  ", null, MANANA, 1L));
        assertEquals("El asunto de la actividad es obligatorio", error.getMessage());
        verify(empresaConsulta, never()).existeEmpresa(any()); // el dominio la rechaza antes
    }

    @Test
    @DisplayName("Completar una actividad pendiente la marca como completada")
    void completarActividad() {
        when(actividadRepository.buscarPorId(1L)).thenReturn(Optional.of(llamada(1L, MANANA, false)));
        guardarDevuelveConId();

        Actividad resultado = actividadService.completar(1L);

        assertTrue(resultado.isCompletada());
    }

    @Test
    @DisplayName("Completar una actividad ya completada lanza 409 y no guarda")
    void completarDosVeces() {
        when(actividadRepository.buscarPorId(1L)).thenReturn(Optional.of(llamada(1L, MANANA, true)));

        assertThrows(ActividadYaCompletadaException.class, () -> actividadService.completar(1L));
        verify(actividadRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Vencidas solo devuelve las pendientes con fecha pasada")
    void listarVencidas() {
        // el repositorio devuelve las pendientes: una de ayer (vencida) y una de mañana (no)
        when(actividadRepository.buscarPendientes())
                .thenReturn(List.of(llamada(1L, AYER, false), llamada(2L, MANANA, false)));

        List<Actividad> resultado = actividadService.listarVencidas();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId()); // solo la de ayer
    }

    @Test
    @DisplayName("Borrar una actividad que no existe lanza 404 y no borra")
    void borrarInexistente() {
        when(actividadRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ActividadNoEncontradaException.class, () -> actividadService.borrar(99L));
        verify(actividadRepository, never()).borrar(any());
    }
}
