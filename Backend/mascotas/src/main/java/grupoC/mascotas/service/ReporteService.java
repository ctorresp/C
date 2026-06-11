package grupoC.mascotas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.factory.ReporteFactory;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

private final ReporteRepository reporteRepository;
    private final ReporteFactory reporteFactory; 

    public ReporteResponseDTO crearReporte(ReporteRequestDTO dto, String usuarioUuid) {
        Reporte reporte = reporteFactory.instanciarReporte(dto, usuarioUuid);

        Reporte reporteGuardado = reporteRepository.save(reporte);
        
        return mapToDto(reporteGuardado);
    }

    public List<ReporteResponseDTO> obtenerTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReporteResponseDTO mapToDto(Reporte reporte) {
        return new ReporteResponseDTO(
                reporte.getId(),
                reporte.getMascotaId(),
                reporte.getUsuarioUuid(),
                reporte.getTipoReporte(),
                reporte.getFechaSuceso(),
                reporte.getEstado()
        );
    }

    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con ID: " + id);
        }
        reporteRepository.deleteById(id);
    }

}
