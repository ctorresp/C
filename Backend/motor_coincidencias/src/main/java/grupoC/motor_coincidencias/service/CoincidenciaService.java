package grupoC.motor_coincidencias.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class CoincidenciaService {

    private final CoincidenciaRepository repository;
    private final ReporteClient reporteClient;
    private final MascotaClient mascotaClient;
    private final GeolocalizacionClient geoClient;

    public CoincidenciaService(CoincidenciaRepository repository, ReporteClient reporteClient, 
                               MascotaClient mascotaClient, GeolocalizacionClient geoClient) {
        this.repository = repository;
        this.reporteClient = reporteClient;
        this.mascotaClient = mascotaClient;
        this.geoClient = geoClient;
    }

    @Transactional(readOnly = true)
    public List<CoincidenciaDetalleDto> obtenerCoincidenciasEnriquecidas(Long reporteId) {
        // 1. Buscamos las coincidencias base en la BD
        List<Coincidencia> coincidencias = repository.findByReportePerdidoId(reporteId);
        if (coincidencias.isEmpty()) {
            coincidencias = repository.findByReporteEncontradoId(reporteId);
        }

        // 2. Mapeamos y enriquecemos cada coincidencia llamando a los otros microservicios
        return coincidencias.stream().map(this::enriquecerCoincidencia).collect(Collectors.toList());
    }

    private CoincidenciaDetalleDto enriquecerCoincidencia(Coincidencia c) {
        CoincidenciaDetalleDto detalle = new CoincidenciaDetalleDto();
        detalle.setId(c.getId());
        detalle.setPorcentajeSimilitud(c.getPorcentajeSimilitud());
        detalle.setEstado(c.getEstado());
        detalle.setFechaCreacion(c.getFechaCreacion());

        try {
            // --- ENRIQUECER DATA PERDIDA ---
            ReporteExternoDto repPerdido = reporteClient.obtenerPorId(c.getReportePerdidoId());
            detalle.setReportePerdidoId(c.getReportePerdidoId());
            
            MascotaExternoDto petPerdida = mascotaClient.obtenerPorId(repPerdido.getMascotaId());
            detalle.setMascotaPerdida(petPerdida);
            
            UbicacionExternoDto locPerdido = geoClient.obtenerUbicacionPorReporte(c.getReportePerdidoId());
            detalle.setUbicacionPerdido(locPerdido);

            // --- ENRIQUECER DATA ENCONTRADA ---
            ReporteExternoDto repEncontrado = reporteClient.obtenerPorId(c.getReporteEncontradoId());
            detalle.setReporteEncontradoId(c.getReporteEncontradoId());
            
            MascotaExternoDto petEncontrada = mascotaClient.obtenerPorId(repEncontrado.getMascotaId());
            detalle.setMascotaEncontrada(petEncontrada);
            
            UbicacionExternoDto locEncontrado = geoClient.obtenerUbicacionPorReporte(c.getReporteEncontradoId());
            detalle.setUbicacionEncontrado(locEncontrado);

        } catch (Exception e) {
            // Tolerancia a fallos: Si un microservicio externo falla de manera síncrona, 
            // el flujo no se cae por completo, pero dejamos trazas en los logs.
            System.err.println("Error al enriquecer coincidencia ID " + c.getId() + ": " + e.getMessage());
        }

        return detalle;
    }

    @Transactional
    public void actualizarEstadoCoincidencia(Long id, EstadoCoincidencia nuevoEstado) {
        Coincidencia coincidencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coincidencia no encontrada"));
        
        coincidencia.setEstado(nuevoEstado);
        repository.save(coincidencia);

        // Si el dueño confirma que encontró a su mascota, actualizamos la mascota original y limpiamos
        if (nuevoEstado == EstadoCoincidencia.EXITOSA) {
            try {
                // 1. Buscamos el reporte perdido para saber cuál es la mascota asociada
                ReporteExternoDto repPerdido = reporteClient.obtenerPorId(coincidencia.getReportePerdidoId());
                
                // 2. Le avisamos al MS de Mascotas que la mascota ya está a salvo
                mascotaClient.actualizarEstadoMascota(repPerdido.getMascotaId(), "ENCONTRADA");
                System.out.println("Éxito: Mascota " + repPerdido.getMascotaId() + " actualizada a ENCONTRADA.");

                // 👇 3. NUEVO: Borramos los reportes asociados porque ya no son necesarios
                reporteClient.eliminarReporte(coincidencia.getReportePerdidoId());
                reporteClient.eliminarReporte(coincidencia.getReporteEncontradoId());
                System.out.println("Éxito: Reportes limpiados correctamente.");

            } catch (Exception e) {
                // Logueamos en caso de que el otro microservicio falle o no esté disponible
                System.err.println("No se pudo completar el flujo de éxito de la coincidencia: " + e.getMessage());
            }
        }
    }
}