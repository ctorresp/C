package grupoC.geolocalizacion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; // <-- Asegúrate de importar esto

import grupoC.geolocalizacion.dto.MarcadorRequestDTO;
import grupoC.geolocalizacion.dto.MarcadorResponseDTO;
import grupoC.geolocalizacion.model.MarcadorEspacial;
import grupoC.geolocalizacion.repository.MarcadorEspacialRepository;
import grupoC.geolocalizacion.util.OfuscacionUbicacionUtil;

@Service
@RequiredArgsConstructor
public class MarcadorEspacialService {

    private final MarcadorEspacialRepository marcadorRepository;
    
    // Guarda la ubicación REAL (Solo para la base de datos)
    public MarcadorEspacial crearMarcador(MarcadorRequestDTO request) {
        MarcadorEspacial marcador = new MarcadorEspacial();
        marcador.setReporteId(request.reporteId());
        marcador.setTipoMarcador(request.tipoMarcador());
        marcador.setLatitud(request.latitud());
        marcador.setLongitud(request.longitud());
        marcador.setFechaHoraRegistro(LocalDateTime.now());
        
        return marcadorRepository.save(marcador);
    }

    // Devuelve UN marcador ofuscado (Ej: Al entrar a ver el detalle de un reporte)
    public MarcadorResponseDTO obtenerMarcadorPorReporte(Long reporteId) {
        MarcadorEspacial marcador = marcadorRepository.findByReporteId(reporteId)
                .orElseThrow(() -> new RuntimeException("Marcador no encontrado para el reporte ID: " + reporteId));
        
        return convertirAOfuscado(marcador);
    }

    // Devuelve TODOS los marcadores ofuscados (Ej: Para pintar el mapa principal con todos los perritos perdidos)
    public List<MarcadorResponseDTO> obtenerTodosLosMarcadores() {
        return marcadorRepository.findAll().stream()
                .map(this::convertirAOfuscado)
                .collect(Collectors.toList());
    }

    // Helper: Convierte la entidad real al DTO seguro
    private MarcadorResponseDTO convertirAOfuscado(MarcadorEspacial marcador) {
        double[] coordsFalsas = OfuscacionUbicacionUtil.ofuscar(marcador.getLatitud(), marcador.getLongitud());
        
        return new MarcadorResponseDTO(
                marcador.getId(),
                marcador.getReporteId(),
                marcador.getTipoMarcador(),
                coordsFalsas[0], // Latitud movida
                coordsFalsas[1], // Longitud movida
                marcador.getFechaHoraRegistro()
        );
    }

    public void eliminarMarcadorPorReporte(Long reporteId) {
        marcadorRepository.deleteByReporteId(reporteId);
    }

    public void eliminarMarcadoresEnLote(List<Long> reporteIds) {
        if (reporteIds != null && !reporteIds.isEmpty()) {
            marcadorRepository.deleteByReporteIdIn(reporteIds);
        }
    }

}
