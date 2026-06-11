package grupoC.motor_coincidencias.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import grupoC.motor_coincidencias.dto.CoincidenciaDetalleDto;
import grupoC.motor_coincidencias.model.EstadoCoincidencia;
import grupoC.motor_coincidencias.service.CoincidenciaService;

@RestController
@RequestMapping("/coincidencias")
public class CoincidenciaController {

private final CoincidenciaService coincidenciaService;

    public CoincidenciaController(CoincidenciaService coincidenciaService) {
        this.coincidenciaService = coincidenciaService;
    }

    // Obtener todas las coincidencias enriquecidas (BFF) para un reporte específico
    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<List<CoincidenciaDetalleDto>> obtenerPorReporte(@PathVariable Long reporteId) {
        List<CoincidenciaDetalleDto> coincidencias = coincidenciaService.obtenerCoincidenciasEnriquecidas(reporteId);
        return ResponseEntity.ok(coincidencias);
    }

    // Cambiar el estado (Ej: el usuario la descarta o la aprueba)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id, 
            @RequestParam EstadoCoincidencia nuevoEstado) {
        
        coincidenciaService.actualizarEstadoCoincidencia(id, nuevoEstado);
        return ResponseEntity.noContent().build();
    }

}
