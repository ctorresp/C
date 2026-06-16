package grupoC.mascotas.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import grupoC.mascotas.client.GeolocalizacionClient; 
import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.factory.ReporteFactory;
import grupoC.mascotas.model.Estado; // <-- Asegúrate de importar el Enum Estado
import grupoC.mascotas.model.Mascota;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.MascotaRepository; 
import grupoC.mascotas.repository.ReporteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final MascotaRepository mascotaRepository;
    private final ReporteFactory reporteFactory; 
    private final GeolocalizacionClient geolocalizacionClient;

    @Transactional
    public ReporteResponseDTO crearReporte(ReporteRequestDTO dto, String usuarioUuid) {
        Mascota mascota = mascotaRepository.findById(dto.mascotaId()) 
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + dto.mascotaId()));

        Reporte reporte = reporteFactory.instanciarReporte(dto, usuarioUuid);
        reporte.setMascota(mascota);

        if (dto.estado() == Estado.PERDIDA) {
            mascota.setEstado(Estado.PERDIDA);
            mascotaRepository.save(mascota);
        }

        Reporte reporteGuardado = reporteRepository.save(reporte);
        return mapToDto(reporteGuardado);
    }

    public List<ReporteResponseDTO> obtenerTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReporteResponseDTO obtenerPorId(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
        return mapToDto(reporte);
    }

    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        
        // 1. Primero borramos los puntos en el mapa vía Feign
        try {
            geolocalizacionClient.eliminarMarcadoresPorReporte(id);
        } catch (Exception e) {
            System.err.println("No se pudo borrar el marcador en geolocalización: " + e.getMessage());
        }

        // 2. Borramos el reporte de nuestra base de datos
        reporteRepository.deleteById(id);
    }

    private ReporteResponseDTO mapToDto(Reporte reporte) {
        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getMascota().getId(), 
                reporte.getUsuarioUuid(),
                reporte.getTipoReporte(),
                reporte.getFechaSuceso(),
                reporte.getEstado()
        );
    }
}