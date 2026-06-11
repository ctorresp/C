package grupoC.motor_coincidencias.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Coincidencias", description = "Consulta y gestión de coincidencias entre reportes")
public class CoincidenciaController {

private final CoincidenciaService coincidenciaService;

    public CoincidenciaController(CoincidenciaService coincidenciaService) {
        this.coincidenciaService = coincidenciaService;
    }

    // Obtener todas las coincidencias enriquecidas (BFF) para un reporte específico
    @GetMapping("/reporte/{reporteId}")
    @Operation(summary = "Obtener coincidencias por reporte", description = "Devuelve coincidencias enriquecidas para un reporte")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Coincidencias obtenidas correctamente")
    })
    public ResponseEntity<List<CoincidenciaDetalleDto>> obtenerPorReporte(@PathVariable Long reporteId) {
        List<CoincidenciaDetalleDto> coincidencias = coincidenciaService.obtenerCoincidenciasEnriquecidas(reporteId);
        return ResponseEntity.ok(coincidencias);
    }

    // Cambiar el estado (Ej: el usuario la descarta o la aprueba)
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de coincidencia", description = "Modifica el estado de una coincidencia")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Coincidencia no encontrada")
    })
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id, 
            @RequestParam EstadoCoincidencia nuevoEstado) {
        
        coincidenciaService.actualizarEstadoCoincidencia(id, nuevoEstado);
        return ResponseEntity.noContent().build();
    }

}
