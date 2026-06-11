package grupoC.motor_coincidencias.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import grupoC.motor_coincidencias.dto.CoincidenciaDetalleDto;
import grupoC.motor_coincidencias.model.EstadoCoincidencia;
import grupoC.motor_coincidencias.service.CoincidenciaService;

@ExtendWith(MockitoExtension.class)
public class Motor_coincidenciasControllerTest {

	@Mock
	private CoincidenciaService coincidenciaService;

	@InjectMocks
	private CoincidenciaController coincidenciaController;

	// Verifica que obtenerPorReporte devuelve 200 con la lista de coincidencias enriquecidas.
	@Test
	void obtenerPorReporteDebeRetornarListaConEstadoOk() {
		CoincidenciaDetalleDto detalle = new CoincidenciaDetalleDto();
		detalle.setId(1L);
		detalle.setPorcentajeSimilitud(80.0);
		detalle.setEstado(EstadoCoincidencia.PENDIENTE);
		detalle.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 10, 0));

		when(coincidenciaService.obtenerCoincidenciasEnriquecidas(100L)).thenReturn(List.of(detalle));

		ResponseEntity<List<CoincidenciaDetalleDto>> response = coincidenciaController.obtenerPorReporte(100L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals(1L, response.getBody().get(0).getId());
	}

	// Verifica que actualizarEstado devuelve 204 y delega el cambio al servicio.
	@Test
	void actualizarEstadoDebeRetornarNoContent() {
		ResponseEntity<Void> response = coincidenciaController.actualizarEstado(5L, EstadoCoincidencia.EXITOSA);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(coincidenciaService).actualizarEstadoCoincidencia(5L, EstadoCoincidencia.EXITOSA);
	}
}
