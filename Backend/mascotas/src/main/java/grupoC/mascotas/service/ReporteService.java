package grupoC.mascotas.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import grupoC.mascotas.client.GeolocalizacionClient; // Inyectamos el cliente Feign
import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.factory.ReporteFactory;
import grupoC.mascotas.model.Mascota;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.MascotaRepository; // Necesario para buscar la mascota
import grupoC.mascotas.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final MascotaRepository mascotaRepository;
    private final ReporteFactory reporteFactory; 
    private final GeolocalizacionClient geolocalizacionClient; // <-- NUEVO

    public ReporteResponseDTO crearReporte(ReporteRequestDTO dto, String usuarioUuid) {
        // 1. Buscamos la mascota de la BD interna
        Mascota mascota = mascotaRepository.findById(dto.mascotaId()) // Asumiendo que tu DTO tiene mascotaId()
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + dto.mascotaId()));

        Reporte reporte = reporteFactory.instanciarReporte(dto, usuarioUuid);
        reporte.setMascota(mascota); // 2. Vinculamos el objeto real

        Reporte reporteGuardado = reporteRepository.save(reporte);
        return mapToDto(reporteGuardado);
    }

    public List<ReporteResponseDTO> obtenerTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        
        // 1. Primero borramos los puntos en el mapa vía Feign
        try {
            geolocalizacionClient.eliminarMarcadoresPorReporte(id);
        } catch (Exception e) {
            // Opcional: Loggear el error si el microservicio de mapas está caído, 
            // decides si frenar la eliminación o continuar.
            System.err.println("No se pudo borrar el marcador en geolocalización: " + e.getMessage());
        }

        // 2. Borramos el reporte de nuestra base de datos
        reporteRepository.deleteById(id);
    }

    private ReporteResponseDTO mapToDto(Reporte reporte) {
        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getMascota().getId(), // Accedemos de forma segura a través del objeto
                reporte.getUsuarioUuid(),
                reporte.getTipoReporte(),
                reporte.getFechaSuceso(),
                reporte.getEstado()
        );
    }
}