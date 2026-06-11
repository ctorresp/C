package grupoC.motor_coincidencias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import grupoC.motor_coincidencias.client.GeolocalizacionClient;
import grupoC.motor_coincidencias.client.MascotaClient;
import grupoC.motor_coincidencias.client.ReporteClient;
import grupoC.motor_coincidencias.dto.CoincidenciaDetalleDto;
import grupoC.motor_coincidencias.dto.MascotaExternoDto;
import grupoC.motor_coincidencias.dto.ReporteExternoDto;
import grupoC.motor_coincidencias.dto.UbicacionExternoDto;
import grupoC.motor_coincidencias.model.Coincidencia;
import grupoC.motor_coincidencias.model.EstadoCoincidencia;
import grupoC.motor_coincidencias.repository.CoincidenciaRepository;

@ExtendWith(MockitoExtension.class)
public class Motor_coincidenciasServiceTest {

	@Mock
	private CoincidenciaRepository repository;

	@Mock
	private ReporteClient reporteClient;

	@Mock
	private MascotaClient mascotaClient;

	@Mock
	private GeolocalizacionClient geoClient;

	@InjectMocks
	private CoincidenciaService coincidenciaService;

	// Verifica que obtiene coincidencias por reporte perdido y las enriquece con datos externos.
	@Test
	void obtenerCoincidenciasEnriquecidasDebeEnriquecerCuandoExisteReportePerdido() {
		Coincidencia coincidencia = coincidencia(1L, 100L, 200L, 87.5, EstadoCoincidencia.PENDIENTE);

		ReporteExternoDto repPerdido = reporte(100L, 10L, "PERDIDA");
		ReporteExternoDto repEncontrado = reporte(200L, 20L, "ENCONTRADA");
		MascotaExternoDto petPerdida = mascota(10L, "Perro", "Labrador");
		MascotaExternoDto petEncontrada = mascota(20L, "Perro", "Labrador");
		UbicacionExternoDto locPerdida = ubicacion(1000L, -34.60, -58.38);
		UbicacionExternoDto locEncontrada = ubicacion(2000L, -34.61, -58.39);

		when(repository.findByReportePerdidoId(100L)).thenReturn(List.of(coincidencia));
		when(reporteClient.obtenerPorId(100L)).thenReturn(repPerdido);
		when(reporteClient.obtenerPorId(200L)).thenReturn(repEncontrado);
		when(mascotaClient.obtenerPorId(10L)).thenReturn(petPerdida);
		when(mascotaClient.obtenerPorId(20L)).thenReturn(petEncontrada);
		when(geoClient.obtenerUbicacionPorReporte(100L)).thenReturn(locPerdida);
		when(geoClient.obtenerUbicacionPorReporte(200L)).thenReturn(locEncontrada);

		List<CoincidenciaDetalleDto> resultado = coincidenciaService.obtenerCoincidenciasEnriquecidas(100L);

		assertEquals(1, resultado.size());
		CoincidenciaDetalleDto detalle = resultado.get(0);
		assertEquals(1L, detalle.getId());
		assertEquals(87.5, detalle.getPorcentajeSimilitud());
		assertEquals(100L, detalle.getReportePerdidoId());
		assertEquals(200L, detalle.getReporteEncontradoId());
		assertEquals("Labrador", detalle.getMascotaPerdida().getRaza());
		assertEquals("Labrador", detalle.getMascotaEncontrada().getRaza());
		assertEquals(-34.60, detalle.getUbicacionPerdido().getLatitudOfuscada());
		assertEquals(-58.39, detalle.getUbicacionEncontrado().getLongitudOfuscada());
	}

	// Verifica que usa búsqueda por reporte encontrado cuando no hay resultados en reporte perdido.
	@Test
	void obtenerCoincidenciasEnriquecidasDebeUsarFallbackPorReporteEncontrado() {
		Coincidencia coincidencia = coincidencia(2L, 101L, 201L, 70.0, EstadoCoincidencia.REVISADA);

		when(repository.findByReportePerdidoId(201L)).thenReturn(List.of());
		when(repository.findByReporteEncontradoId(201L)).thenReturn(List.of(coincidencia));
		when(reporteClient.obtenerPorId(101L)).thenReturn(reporte(101L, 11L, "PERDIDA"));
		when(reporteClient.obtenerPorId(201L)).thenReturn(reporte(201L, 21L, "ENCONTRADA"));
		when(mascotaClient.obtenerPorId(11L)).thenReturn(mascota(11L, "Gato", "Siames"));
		when(mascotaClient.obtenerPorId(21L)).thenReturn(mascota(21L, "Gato", "Siames"));
		when(geoClient.obtenerUbicacionPorReporte(101L)).thenReturn(ubicacion(1L, -34.5, -58.5));
		when(geoClient.obtenerUbicacionPorReporte(201L)).thenReturn(ubicacion(2L, -34.6, -58.6));

		List<CoincidenciaDetalleDto> resultado = coincidenciaService.obtenerCoincidenciasEnriquecidas(201L);

		assertEquals(1, resultado.size());
		verify(repository).findByReportePerdidoId(201L);
		verify(repository).findByReporteEncontradoId(201L);
	}

	// Verifica tolerancia a fallos cuando una integración externa lanza excepción.
	@Test
	void obtenerCoincidenciasEnriquecidasDebeRetornarBaseAunqueFalleEnriquecimiento() {
		Coincidencia coincidencia = coincidencia(3L, 102L, 202L, 55.0, EstadoCoincidencia.PENDIENTE);

		when(repository.findByReportePerdidoId(102L)).thenReturn(List.of(coincidencia));
		when(reporteClient.obtenerPorId(102L)).thenThrow(new RuntimeException("servicio caido"));

		List<CoincidenciaDetalleDto> resultado = coincidenciaService.obtenerCoincidenciasEnriquecidas(102L);

		assertEquals(1, resultado.size());
		CoincidenciaDetalleDto detalle = resultado.get(0);
		assertEquals(3L, detalle.getId());
		assertEquals(55.0, detalle.getPorcentajeSimilitud());
		assertNull(detalle.getMascotaPerdida());
		assertNull(detalle.getMascotaEncontrada());
	}

	// Verifica que actualizarEstadoCoincidencia cambia estado y persiste la entidad.
	@Test
	void actualizarEstadoCoincidenciaDebeGuardarNuevoEstado() {
		Coincidencia coincidencia = coincidencia(4L, 103L, 203L, 90.0, EstadoCoincidencia.PENDIENTE);
		when(repository.findById(4L)).thenReturn(Optional.of(coincidencia));

		coincidenciaService.actualizarEstadoCoincidencia(4L, EstadoCoincidencia.EXITOSA);

		assertEquals(EstadoCoincidencia.EXITOSA, coincidencia.getEstado());
		verify(repository).save(coincidencia);
	}

	// Verifica que actualizarEstadoCoincidencia lanza excepción si no encuentra la coincidencia.
	@Test
	void actualizarEstadoCoincidenciaDebeLanzarExcepcionSiNoExiste() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> coincidenciaService.actualizarEstadoCoincidencia(99L, EstadoCoincidencia.DESCARTADA));

		assertNotNull(exception);
		assertEquals("Coincidencia no encontrada", exception.getMessage());
	}

	private Coincidencia coincidencia(Long id, Long perdidoId, Long encontradoId, Double similitud, EstadoCoincidencia estado) {
		Coincidencia c = new Coincidencia();
		c.setId(id);
		c.setReportePerdidoId(perdidoId);
		c.setReporteEncontradoId(encontradoId);
		c.setPorcentajeSimilitud(similitud);
		c.setEstado(estado);
		c.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 10, 0));
		return c;
	}

	private ReporteExternoDto reporte(Long id, Long mascotaId, String estado) {
		ReporteExternoDto r = new ReporteExternoDto();
		r.setId(id);
		r.setMascotaId(mascotaId);
		r.setEstado(estado);
		return r;
	}

	private MascotaExternoDto mascota(Long id, String especie, String raza) {
		MascotaExternoDto m = new MascotaExternoDto();
		m.setId(id);
		m.setEspecie(especie);
		m.setRaza(raza);
		return m;
	}

	private UbicacionExternoDto ubicacion(Long id, Double lat, Double lon) {
		UbicacionExternoDto u = new UbicacionExternoDto();
		u.setId(id);
		u.setLatitudOfuscada(lat);
		u.setLongitudOfuscada(lon);
		return u;
	}
}
