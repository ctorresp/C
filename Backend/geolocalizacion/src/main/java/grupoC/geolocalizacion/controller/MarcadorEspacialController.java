package grupoC.geolocalizacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import grupoC.geolocalizacion.dto.MarcadorRequestDTO;
import grupoC.geolocalizacion.dto.MarcadorResponseDTO;
import grupoC.geolocalizacion.service.MarcadorEspacialService;

@RestController
@RequestMapping("/api/geolocalizacion/marcadores")
@RequiredArgsConstructor
public class MarcadorEspacialController {

    private final MarcadorEspacialService marcadorService;

    // POST: Para guardar una nueva ubicación
    @PostMapping
    public ResponseEntity<Map<String, String>> crearMarcador(@RequestBody MarcadorRequestDTO request) {
        marcadorService.crearMarcador(request);
        // Devolvemos un mensaje simple para no exponer los datos reales creados
        return new ResponseEntity<>(Map.of("mensaje", "Marcador guardado exitosamente"), HttpStatus.CREATED);
    }

    // GET: Para ver un marcador específico (Ofuscado)
    @GetMapping("/reporte/{reporteId}")
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
    public ResponseEntity<List<MarcadorResponseDTO>> obtenerTodos() {
        List<MarcadorResponseDTO> marcadores = marcadorService.obtenerTodosLosMarcadores();
        return ResponseEntity.ok(marcadores);
    }

}
