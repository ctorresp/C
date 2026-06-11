package grupoC.geolocalizacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import grupoC.geolocalizacion.dto.MarcadorRequestDTO;
import grupoC.geolocalizacion.dto.MarcadorResponseDTO;
import grupoC.geolocalizacion.model.MarcadorEspacial;
import grupoC.geolocalizacion.repository.MarcadorEspacialRepository;

@ExtendWith(MockitoExtension.class)
public class GeolocalizacionServiceTest {

	@Mock
	private MarcadorEspacialRepository marcadorRepository;

	@InjectMocks
	private MarcadorEspacialService marcadorEspacialService;

	// Verifica que crearMarcador persiste los datos recibidos y asigna fecha de registro.
	@Test
	void crearMarcadorDebeGuardarEntidadConDatosDelRequest() {
		MarcadorRequestDTO request = new MarcadorRequestDTO(10L, "PERDIDA", -34.6037, -58.3816);

		when(marcadorRepository.save(org.mockito.ArgumentMatchers.any(MarcadorEspacial.class)))
				.thenAnswer(invocation -> {
					MarcadorEspacial m = invocation.getArgument(0);
					m.setId(1L);
					return m;
				});

		MarcadorEspacial resultado = marcadorEspacialService.crearMarcador(request);

		ArgumentCaptor<MarcadorEspacial> captor = ArgumentCaptor.forClass(MarcadorEspacial.class);
		verify(marcadorRepository).save(captor.capture());

		MarcadorEspacial guardado = captor.getValue();
		assertEquals(10L, guardado.getReporteId());
		assertEquals("PERDIDA", guardado.getTipoMarcador());
		assertEquals(-34.6037, guardado.getLatitud());
		assertEquals(-58.3816, guardado.getLongitud());
		assertNotNull(guardado.getFechaHoraRegistro());
		assertEquals(1L, resultado.getId());
	}

	// Verifica que obtenerMarcadorPorReporte devuelve el marcador ofuscado cuando existe.
	@Test
	void obtenerMarcadorPorReporteDebeRetornarDtoOfuscado() {
		MarcadorEspacial marcador = marcador(5L, 77L, "ENCONTRADA", -34.6037, -58.3816,
				LocalDateTime.of(2026, 1, 1, 12, 0));
		when(marcadorRepository.findByReporteId(77L)).thenReturn(Optional.of(marcador));

		MarcadorResponseDTO response = marcadorEspacialService.obtenerMarcadorPorReporte(77L);

		assertEquals(5L, response.id());
		assertEquals(77L, response.reporteId());
		assertEquals("ENCONTRADA", response.tipoMarcador());
		assertEquals(LocalDateTime.of(2026, 1, 1, 12, 0), response.fechaHoraRegistro());
		assertNotNull(response.latitudOfuscada());
		assertNotNull(response.longitudOfuscada());
		assertTrue(Math.abs(response.latitudOfuscada() - (-34.6037)) < 0.02);
		assertTrue(Math.abs(response.longitudOfuscada() - (-58.3816)) < 0.02);
	}

	// Verifica que obtenerMarcadorPorReporte lanza excepción cuando no existe el reporte.
	@Test
	void obtenerMarcadorPorReporteDebeLanzarExcepcionCuandoNoExiste() {
		when(marcadorRepository.findByReporteId(999L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> marcadorEspacialService.obtenerMarcadorPorReporte(999L));

		assertEquals("Marcador no encontrado para el reporte ID: 999", exception.getMessage());
	}

	// Verifica que obtenerTodosLosMarcadores transforma la lista de entidades a DTOs ofuscados.
	@Test
	void obtenerTodosLosMarcadoresDebeRetornarListaOfuscada() {
		MarcadorEspacial m1 = marcador(1L, 101L, "PERDIDA", -34.60, -58.38, LocalDateTime.of(2026, 1, 1, 10, 0));
		MarcadorEspacial m2 = marcador(2L, 202L, "ENCONTRADA", -34.61, -58.39,
				LocalDateTime.of(2026, 1, 2, 11, 0));
		when(marcadorRepository.findAll()).thenReturn(List.of(m1, m2));

		List<MarcadorResponseDTO> response = marcadorEspacialService.obtenerTodosLosMarcadores();

		assertEquals(2, response.size());
		assertEquals(101L, response.get(0).reporteId());
		assertEquals(202L, response.get(1).reporteId());
		assertNotNull(response.get(0).latitudOfuscada());
		assertNotNull(response.get(1).longitudOfuscada());
	}

	private MarcadorEspacial marcador(Long id, Long reporteId, String tipo, Double lat, Double lon, LocalDateTime fecha) {
		MarcadorEspacial marcador = new MarcadorEspacial();
		marcador.setId(id);
		marcador.setReporteId(reporteId);
		marcador.setTipoMarcador(tipo);
		marcador.setLatitud(lat);
		marcador.setLongitud(lon);
		marcador.setFechaHoraRegistro(fecha);
		return marcador;
	}
}
