package grupoC.mascotas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.service.ReporteService;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService reporteService;

    @Mock
    private Principal principal;

    @InjectMocks
    private ReporteController reporteController;

    // Verifica que crearReporte devuelve 201 y delega al servicio con el usuario autenticado.
    @Test
    void crearReporteDebeRetornarCreatedConReporteCreado() {
        ReporteRequestDTO request = new ReporteRequestDTO(10L, "PERDIDA", Estado.PERDIDA);
        ReporteResponseDTO responseDto = new ReporteResponseDTO(
                1L, 10L, "user-123", "PERDIDA", LocalDateTime.of(2026, 1, 1, 10, 0), Estado.PERDIDA);

        when(principal.getName()).thenReturn("user-123");
        when(reporteService.crearReporte(request, "user-123")).thenReturn(responseDto);

        ResponseEntity<ReporteResponseDTO> response = reporteController.crearReporte(request, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        verify(reporteService).crearReporte(request, "user-123");
    }

    // Verifica que obtenerReportes devuelve 200 con la lista de reportes.
    @Test
    void obtenerReportesDebeRetornarListado() {
        List<ReporteResponseDTO> reportes = List.of(
                new ReporteResponseDTO(1L, 10L, "user-1", "PERDIDA", LocalDateTime.of(2026, 1, 1, 10, 0), Estado.PERDIDA),
                new ReporteResponseDTO(2L, 20L, "user-2", "ENCONTRADA", LocalDateTime.of(2026, 1, 2, 11, 0), Estado.ENCONTRADA));
        when(reporteService.obtenerTodos()).thenReturn(reportes);

        ResponseEntity<List<ReporteResponseDTO>> response = reporteController.obtenerReportes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("PERDIDA", response.getBody().get(0).tipoReporte());
    }

    // Verifica que eliminarReporte devuelve 204 y delega la eliminación.
    @Test
    void eliminarReporteDebeRetornarNoContent() {
        ResponseEntity<Void> response = reporteController.eliminarReporte(3L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reporteService).eliminarReporte(3L);
    }
}