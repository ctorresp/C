package grupoC.mascotas.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.dto.ReporteResponseDTO;
import grupoC.mascotas.service.ReporteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

private final ReporteService reporteService;

    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crearReporte(@RequestBody ReporteRequestDTO reporteDto, Principal principal) {
        String usuarioUuid = principal.getName();
        ReporteResponseDTO nuevoReporte = reporteService.crearReporte(reporteDto, usuarioUuid);
        return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> obtenerReportes() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        reporteService.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }

}
