package grupoC.mascotas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import grupoC.mascotas.client.GeolocalizacionClient;
import grupoC.mascotas.dto.MascotaRequestDTO;
import grupoC.mascotas.dto.MascotaResponseDTO;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.model.Mascota;
import grupoC.mascotas.repository.ImagenMascotaRepository;
import grupoC.mascotas.repository.MascotaRepository;

@ExtendWith(MockitoExtension.class)
public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private ImagenMascotaRepository imagenRepository;

    @Mock
    private GeolocalizacionClient geolocalizacionClient;

    @InjectMocks
    private MascotaService mascotaService;

    // Verifica que registrarMascota persiste y devuelve el DTO con los datos correctos.
    @Test
    void registrarMascotaDebeGuardarYRetornarDto() {
        MascotaRequestDTO request = new MascotaRequestDTO(
                "Firulais", "Perro", "Mestizo", 3, "Macho", "Marron", "Muy amistoso", Estado.PERDIDA);

        Mascota guardada = mascota(1L, "user-123", "Firulais", "Perro", "Mestizo", 3, "Macho", "Marron",
                "Muy amistoso", Estado.PERDIDA);
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(guardada);

        MascotaResponseDTO response = mascotaService.registrarMascota(request, "user-123");

        assertEquals(1L, response.id());
        assertEquals("user-123", response.usuarioUuid());
        assertEquals("Firulais", response.nombre());
        assertEquals(Estado.PERDIDA, response.estado());
        verify(mascotaRepository).save(any(Mascota.class));
    }

    // Verifica que obtenerTodas transforma toda la lista de entidades a DTOs.
    @Test
    void obtenerTodasDebeRetornarListaDeDtos() {
        Mascota m1 = mascota(1L, "user-1", "Luna", "Gato", "Siames", 2, "Hembra", "Blanco", "Calmada",
                Estado.ENCONTRADA);
        Mascota m2 = mascota(2L, "user-2", "Toby", "Perro", "Caniche", 4, "Macho", "Negro", "Jugueton",
                Estado.PERDIDA);
        when(mascotaRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MascotaResponseDTO> response = mascotaService.obtenerTodas();

        assertEquals(2, response.size());
        assertEquals("Luna", response.get(0).nombre());
        assertEquals("Toby", response.get(1).nombre());
    }

    // Verifica que obtenerPorUsuario filtra por usuario y transforma el resultado a DTOs.
    @Test
    void obtenerPorUsuarioDebeRetornarMascotasDelUsuario() {
        Mascota mascota = mascota(10L, "user-abc", "Nina", "Perro", "Labrador", 1, "Hembra", "Dorado",
                "Cachorra", Estado.PERDIDA);
        when(mascotaRepository.findAllByUsuarioUuid("user-abc")).thenReturn(List.of(mascota));

        List<MascotaResponseDTO> response = mascotaService.obtenerPorUsuario("user-abc");

        assertEquals(1, response.size());
        assertEquals("user-abc", response.get(0).usuarioUuid());
        assertEquals("Nina", response.get(0).nombre());
    }

    // Verifica que obtenerPorId devuelve el DTO cuando la mascota existe.
    @Test
    void obtenerPorIdDebeRetornarDtoCuandoExiste() {
        Mascota mascota = mascota(5L, "user-xyz", "Rocky", "Perro", "Boxer", 6, "Macho", "Atigrado",
                "Activo", Estado.ENCONTRADA);
        when(mascotaRepository.findById(5L)).thenReturn(Optional.of(mascota));

        MascotaResponseDTO response = mascotaService.obtenerPorId(5L);

        assertEquals(5L, response.id());
        assertEquals("Rocky", response.nombre());
        assertEquals(Estado.ENCONTRADA, response.estado());
    }

    // Verifica que obtenerPorId lanza excepción cuando no existe la mascota.
    @Test
    void obtenerPorIdDebeLanzarExcepcionCuandoNoExiste() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mascotaService.obtenerPorId(99L));

        assertEquals("Mascota no encontrada con id: 99", exception.getMessage());
    }

    private Mascota mascota(Long id, String usuarioUuid, String nombre, String especie, String raza, Integer edad,
            String sexo, String color, String descripcion, Estado estado) {
        Mascota mascota = new Mascota();
        mascota.setId(id);
        mascota.setUsuarioUuid(usuarioUuid);
        mascota.setNombre(nombre);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);
        mascota.setEdad(edad);
        mascota.setSexo(sexo);
        mascota.setColor(color);
        mascota.setDescripcion(descripcion);
        mascota.setEstado(estado);
        return mascota;
    }
}