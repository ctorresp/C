package grupoC.mascotas.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import grupoC.mascotas.dto.MascotaRequestDTO;
import grupoC.mascotas.dto.MascotaResponseDTO;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.service.MascotaService;

@ExtendWith(MockitoExtension.class)
public class MascotaControllerTest {

	@Mock
	private MascotaService mascotaService;

	@Mock
	private Principal principal;

	@InjectMocks
	private MascotaController mascotaController;

	// Verifica que crearMascota devuelve 201 y usa el usuario del principal.
	@Test
	void crearMascotaDebeRetornarCreatedConMascotaRegistrada() {
		MascotaRequestDTO request = new MascotaRequestDTO(
				"Firulais", "Perro", "Mestizo", 3, "Macho", "Marron", "Muy amistoso", Estado.PERDIDA);
		MascotaResponseDTO responseDto = new MascotaResponseDTO(
				1L, "user-123", "Firulais", "Perro", "Mestizo", 3, "Macho", "Marron", "Muy amistoso", Estado.PERDIDA, "http://localhost:8081/imagenes/mock.jpg");

		when(principal.getName()).thenReturn("user-123");
		when(mascotaService.registrarMascota(request, "user-123")).thenReturn(responseDto);

		ResponseEntity<MascotaResponseDTO> response = mascotaController.crearMascota(request, principal);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Firulais", response.getBody().nombre());
		verify(mascotaService).registrarMascota(request, "user-123");
	}

	// Verifica que obtenerMascotas devuelve la lista retornada por el servicio.
	@Test
	void obtenerMascotasDebeRetornarListado() {
		List<MascotaResponseDTO> mascotas = List.of(
				new MascotaResponseDTO(1L, "user-1", "Luna", "Gato", "Siames", 2, "Hembra", "Blanco", "Calmada", Estado.ENCONTRADA, "http://localhost:8081/imagenes/mock.jpg"),
				new MascotaResponseDTO(2L, "user-2", "Toby", "Perro", "Caniche", 4, "Macho", "Negro", "Jugueton", Estado.PERDIDA, "http://localhost:8081/imagenes/mock.jpg"));
		when(mascotaService.obtenerTodas()).thenReturn(mascotas);

		ResponseEntity<List<MascotaResponseDTO>> response = mascotaController.obtenerMascotas();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
		assertEquals("Luna", response.getBody().get(0).nombre());
	}

	// Verifica que obtenerMascotaPorId devuelve 200 con la mascota solicitada.
	@Test
	void obtenerMascotaPorIdDebeRetornarMascota() {
		MascotaResponseDTO dto = new MascotaResponseDTO(
				5L, "user-xyz", "Rocky", "Perro", "Boxer", 6, "Macho", "Atigrado", "Activo", Estado.ENCONTRADA, "http://localhost:8081/imagenes/mock.jpg");
		when(mascotaService.obtenerPorId(5L)).thenReturn(dto);

		ResponseEntity<MascotaResponseDTO> response = mascotaController.obtenerMascotaPorId(5L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(5L, response.getBody().id());
		assertEquals("Rocky", response.getBody().nombre());
	}
}
