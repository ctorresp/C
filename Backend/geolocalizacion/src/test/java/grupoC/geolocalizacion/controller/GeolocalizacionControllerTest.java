package grupoC.geolocalizacion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import grupoC.geolocalizacion.dto.MarcadorRequestDTO;
import grupoC.geolocalizacion.dto.MarcadorResponseDTO;
import grupoC.geolocalizacion.service.MarcadorEspacialService;

@ExtendWith(MockitoExtension.class)
public class GeolocalizacionControllerTest {

	@Mock
	private MarcadorEspacialService marcadorService;

	@InjectMocks
	private MarcadorEspacialController marcadorController;

	// Verifica que crearMarcador responde 201 con mensaje y delega en el servicio.
	@Test
	void crearMarcadorDebeRetornarCreatedConMensaje() {
		MarcadorRequestDTO request = new MarcadorRequestDTO(10L, "PERDIDA", -34.6037, -58.3816);

		ResponseEntity<Map<String, String>> response = marcadorController.crearMarcador(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Marcador guardado exitosamente", response.getBody().get("mensaje"));
		verify(marcadorService).crearMarcador(request);
	}

	// Verifica que obtenerMarcadorPorReporte devuelve 200 cuando el marcador existe.
	@Test
	void obtenerMarcadorPorReporteDebeRetornarOkCuandoExiste() {
		MarcadorResponseDTO dto = new MarcadorResponseDTO(
				1L, 10L, "ENCONTRADA", -34.60, -58.38, LocalDateTime.of(2026, 1, 1, 12, 0));
		when(marcadorService.obtenerMarcadorPorReporte(10L)).thenReturn(dto);

		ResponseEntity<MarcadorResponseDTO> response = marcadorController.obtenerMarcadorPorReporte(10L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(10L, response.getBody().reporteId());
		assertEquals("ENCONTRADA", response.getBody().tipoMarcador());
	}

	// Verifica que obtenerMarcadorPorReporte devuelve 404 cuando el servicio lanza error.
	@Test
	void obtenerMarcadorPorReporteDebeRetornarNotFoundCuandoNoExiste() {
		when(marcadorService.obtenerMarcadorPorReporte(999L))
				.thenThrow(new RuntimeException("Marcador no encontrado para el reporte ID: 999"));

		ResponseEntity<MarcadorResponseDTO> response = marcadorController.obtenerMarcadorPorReporte(999L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	// Verifica que obtenerTodos devuelve 200 con la lista ofuscada de marcadores.
	@Test
	void obtenerTodosDebeRetornarListadoDeMarcadores() {
		List<MarcadorResponseDTO> lista = List.of(
				new MarcadorResponseDTO(1L, 10L, "PERDIDA", -34.60, -58.38, LocalDateTime.of(2026, 1, 1, 10, 0)),
				new MarcadorResponseDTO(2L, 20L, "ENCONTRADA", -34.61, -58.39, LocalDateTime.of(2026, 1, 2, 11, 0)));
		when(marcadorService.obtenerTodosLosMarcadores()).thenReturn(lista);

		ResponseEntity<List<MarcadorResponseDTO>> response = marcadorController.obtenerTodos();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
		assertEquals("PERDIDA", response.getBody().get(0).tipoMarcador());
	}
}
