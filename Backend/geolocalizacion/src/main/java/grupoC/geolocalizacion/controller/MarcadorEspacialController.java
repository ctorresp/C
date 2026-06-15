package grupoC.geolocalizacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import grupoC.geolocalizacion.dto.MarcadorRequestDTO;
import grupoC.geolocalizacion.dto.MarcadorResponseDTO;
import grupoC.geolocalizacion.service.MarcadorEspacialService;

@RestController
@RequestMapping("/api/geolocalizacion/marcadores")
@RequiredArgsConstructor
@Tag(name = "Geolocalizacion", description = "Operaciones de registro y consulta de marcadores espaciales")
public class MarcadorEspacialController {

    private final MarcadorEspacialService marcadorService;

    // POST: Para guardar una nueva ubicación
    @PostMapping
    @Operation(summary = "Crear marcador", description = "Registra un marcador geográfico asociado a un reporte")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Marcador guardado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Map<String, String>> crearMarcador(@RequestBody MarcadorRequestDTO request) {
        marcadorService.crearMarcador(request);
        // Devolvemos un mensaje simple para no exponer los datos reales creados
        return new ResponseEntity<>(Map.of("mensaje", "Marcador guardado exitosamente"), HttpStatus.CREATED);
    }

    // GET: Para ver un marcador específico (Ofuscado)
    @GetMapping("/reporte/{reporteId}")
    @Operation(summary = "Obtener marcador por reporte", description = "Devuelve la ubicación ofuscada de un reporte")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Marcador encontrado", content = @Content(schema = @Schema(implementation = MarcadorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Marcador no encontrado")
    })
    public ResponseEntity<MarcadorResponseDTO> obtenerMarcadorPorReporte(@PathVariable Long reporteId) {
        try {
            MarcadorResponseDTO marcador = marcadorService.obtenerMarcadorPorReporte(reporteId);
            return ResponseEntity.ok(marcador);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si no existe, devolvemos 404
        }
    }

    // GET: Para ver todo el mapa (Ofuscado)
    @GetMapping
    @Operation(summary = "Listar marcadores", description = "Devuelve todas las ubicaciones ofuscadas")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<List<MarcadorResponseDTO>> obtenerTodos() {
        List<MarcadorResponseDTO> marcadores = marcadorService.obtenerTodosLosMarcadores();
        return ResponseEntity.ok(marcadores);
    }

    @DeleteMapping("/reporte/{reporteId}")
    @Operation(summary = "Eliminar marcador por reporte", description = "Elimina un marcador dado el ID de su reporte")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminarMarcadoresPorReporte(@PathVariable Long reporteId) {
        marcadorService.eliminarMarcadorPorReporte(reporteId);
        return ResponseEntity.noContent().build();
    }

    // DELETE: Borrar marcadores en lote
    @DeleteMapping("/reportes/batch")
    @Operation(summary = "Eliminar marcadores en lote", description = "Elimina múltiples marcadores dados los IDs de los reportes")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminarMarcadoresEnLote(@RequestParam("ids") List<Long> reporteIds) {
        marcadorService.eliminarMarcadoresEnLote(reporteIds);
        return ResponseEntity.noContent().build();
    }

}
