package grupoC.mascotas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import grupoC.mascotas.client.GeolocalizacionClient;
import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.factory.ReporteFactory;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.model.Mascota;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.MascotaRepository;
import grupoC.mascotas.repository.ReporteRepository;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private MascotaRepository mascotaRepository; // <-- Agregado

    @Mock
    private ReporteFactory reporteFactory;

    @Mock
    private GeolocalizacionClient geolocalizacionClient; // <-- Agregado para el borrado

    @InjectMocks
    private ReporteService reporteService;

    // Verifica que crearReporte usa la fábrica, vincula la mascota, guarda y devuelve el DTO.
    @Test
    void crearReporteDebeGuardarYRetornarDto() {
        ReporteRequestDTO request = new ReporteRequestDTO(10L, "PERDIDA", Estado.PERDIDA);
        
        // Creamos la mascota dummy que va a devolver el repositorio
        Mascota mascotaDummy = new Mascota();
        mascotaDummy.setId(10L);
        
        Reporte reporteInstanciado = reporte(10L, "user-123", "PERDIDA", Estado.PERDIDA);
        Reporte reporteGuardado = reporte(10L, "user-123", "PERDIDA", Estado.PERDIDA);
        reporteGuardado.setId(1L);

        // Simulamos la búsqueda de la mascota
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaDummy));
        when(reporteFactory.instanciarReporte(request, "user-123")).thenReturn(reporteInstanciado);
        when(reporteRepository.save(reporteInstanciado)).thenReturn(reporteGuardado);

        ReporteResponseDTO response = reporteService.crearReporte(request, "user-123");

        assertEquals(1L, response.id());
        assertEquals(10L, response.mascotaId());
        assertEquals("user-123", response.usuarioUuid());
        assertEquals("PERDIDA", response.tipoReporte());
        assertEquals(Estado.PERDIDA, response.estado());
        
        verify(mascotaRepository).findById(10L);
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
        assertEquals(5L, response.get(0).mascotaId()); // Verificamos que traiga el ID de mascota
        assertEquals("ENCONTRADA", response.get(1).tipoReporte());
    }

    // Verifica que eliminarReporte borra en Feign y el registro cuando el ID existe.
    @Test
    void eliminarReporteDebeEliminarCuandoExiste() {
        when(reporteRepository.existsById(3L)).thenReturn(true);

        reporteService.eliminarReporte(3L);

        // Ahora verificamos también que intente borrar el marcador vía Feign
        verify(geolocalizacionClient).eliminarMarcadoresPorReporte(3L);
        verify(reporteRepository).deleteById(3L);
    }

    // Verifica que eliminarReporte lanza error cuando el ID no existe.
    @Test
    void eliminarReporteDebeLanzarExcepcionCuandoNoExiste() {
        when(reporteRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> reporteService.eliminarReporte(99L));

        assertEquals("Reporte no encontrado con ID: 99", exception.getMessage());
    }

    // MÉTODO DE AYUDA CORREGIDO
    private Reporte reporte(Long mascotaId, String usuarioUuid, String tipoReporte, Estado estado) {
        Reporte reporte = new Reporte();
        
        // CORRECCIÓN: Le asociamos un objeto Mascota real para que mapToDto no tire NullPointer
        Mascota mascota = new Mascota();
        mascota.setId(mascotaId);
        reporte.setMascota(mascota);
        
        reporte.setUsuarioUuid(usuarioUuid);
        reporte.setTipoReporte(tipoReporte);
        reporte.setFechaSuceso(LocalDateTime.of(2026, 1, 1, 10, 0));
        reporte.setEstado(estado);
        return reporte;
    }
}