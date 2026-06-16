package grupoC.mascotas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import grupoC.mascotas.dto.MascotaRequestDTO;
import grupoC.mascotas.dto.MascotaResponseDTO;
import grupoC.mascotas.service.MascotaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "Gestión de mascotas registradas por usuarios")
public class MascotaController {

private final MascotaService mascotaService;

    @PostMapping
    @Operation(summary = "Crear mascota", description = "Registra una mascota asociada al usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mascota creada correctamente", content = @Content(schema = @Schema(implementation = MascotaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<MascotaResponseDTO> crearMascota(@RequestBody MascotaRequestDTO mascotaDto, Principal principal) {
        String usuarioUuid = principal.getName(); 
        MascotaResponseDTO nuevaMascota = mascotaService.registrarMascota(mascotaDto, usuarioUuid);
        return new ResponseEntity<>(nuevaMascota, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Obtiene todas las mascotas registradas")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<List<MascotaResponseDTO>> obtenerMascotas() {
        return ResponseEntity.ok(mascotaService.obtenerTodas());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID", description = "Devuelve la mascota solicitada por su ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota encontrada", content = @Content(schema = @Schema(implementation = MascotaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaResponseDTO> obtenerMascotaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota", description = "Actualiza los datos de una mascota existente")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mascota actualizada correctamente", content = @Content(schema = @Schema(implementation = MascotaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaResponseDTO> actualizarMascota(@PathVariable Long id, @RequestBody MascotaRequestDTO mascotaDto) {
        MascotaResponseDTO mascotaActualizada = mascotaService.actualizarMascota(id, mascotaDto);
        return ResponseEntity.ok(mascotaActualizada);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de mascota", description = "Actualiza únicamente el estado (ENCONTRADA/PERDIDA) de la mascota")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> actualizarEstadoMascota(@PathVariable Long id, @RequestParam grupoC.mascotas.model.Estado estado) {
        mascotaService.actualizarEstadoMascota(id, estado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota del sistema por su ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Mascota eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
