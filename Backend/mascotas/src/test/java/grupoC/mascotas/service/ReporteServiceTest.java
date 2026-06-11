package grupoC.mascotas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.factory.ReporteFactory;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.ReporteRepository;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private ReporteFactory reporteFactory;

    @InjectMocks
    private ReporteService reporteService;

    // Verifica que crearReporte usa la fábrica, guarda y devuelve el DTO esperado.
    @Test
    void crearReporteDebeGuardarYRetornarDto() {
        ReporteRequestDTO request = new ReporteRequestDTO(10L, "PERDIDA", Estado.PERDIDA);
        Reporte reporteInstanciado = reporte(10L, "user-123", "PERDIDA", Estado.PERDIDA);
        Reporte reporteGuardado = reporte(10L, "user-123", "PERDIDA", Estado.PERDIDA);
        reporteGuardado.setId(1L);

        when(reporteFactory.instanciarReporte(request, "user-123")).thenReturn(reporteInstanciado);
        when(reporteRepository.save(reporteInstanciado)).thenReturn(reporteGuardado);

        ReporteResponseDTO response = reporteService.crearReporte(request, "user-123");

        assertEquals(1L, response.id());
        assertEquals(10L, response.mascotaId());
        assertEquals("user-123", response.usuarioUuid());
        assertEquals("PERDIDA", response.tipoReporte());
        assertEquals(Estado.PERDIDA, response.estado());
        verify(reporteFactory).instanciarReporte(request, "user-123");
        verify(reporteRepository).save(reporteInstanciado);
    }

    // Verifica que obtenerTodos transforma correctamente los reportes a DTO.
    @Test
    void obtenerTodosDebeRetornarListaDeDtos() {
        Reporte r1 = reporte(5L, "user-a", "PERDIDA", Estado.PERDIDA);
        r1.setId(1L);
        Reporte r2 = reporte(8L, "user-b", "ENCONTRADA", Estado.ENCONTRADA);
        r2.setId(2L);
        when(reporteRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ReporteResponseDTO> response = reporteService.obtenerTodos();

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("ENCONTRADA", response.get(1).tipoReporte());
    }

    // Verifica que eliminarReporte borra el registro cuando el ID existe.
    @Test
    void eliminarReporteDebeEliminarCuandoExiste() {
        when(reporteRepository.existsById(3L)).thenReturn(true);

        reporteService.eliminarReporte(3L);

        verify(reporteRepository).deleteById(3L);
    }

    // Verifica que eliminarReporte lanza error cuando el ID no existe.
    @Test
    void eliminarReporteDebeLanzarExcepcionCuandoNoExiste() {
        when(reporteRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> reporteService.eliminarReporte(99L));

        assertEquals("Reporte no encontrado con ID: 99", exception.getMessage());
    }

    private Reporte reporte(Long mascotaId, String usuarioUuid, String tipoReporte, Estado estado) {
        Reporte reporte = new Reporte();
        reporte.setMascotaId(mascotaId);
        reporte.setUsuarioUuid(usuarioUuid);
        reporte.setTipoReporte(tipoReporte);
        reporte.setFechaSuceso(LocalDateTime.of(2026, 1, 1, 10, 0));
        reporte.setEstado(estado);
        return reporte;
    }
}