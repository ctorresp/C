package grupoC.mascotas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import grupoC.mascotas.model.ImagenMascota;
import grupoC.mascotas.service.ImagenMascotaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/imagenes")
@RequiredArgsConstructor
@Tag(name = "Imagenes", description = "Carga y administración de imágenes de mascotas")
public class ImagenMascotaController {

    private final ImagenMascotaService imagenService;

@PostMapping("/subir")
    @Operation(summary = "Subir imagen", description = "Sube una imagen y la asocia a una mascota")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Imagen subida correctamente", content = @Content(schema = @Schema(implementation = ImagenMascota.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación o procesamiento"),
        @ApiResponse(responseCode = "500", description = "Error al guardar la imagen")
    })
    public ResponseEntity<?> subirImagen(
            @Parameter(description = "Archivo de imagen", required = true)
            @RequestParam("archivo") MultipartFile archivo,
            @Parameter(description = "ID de la mascota", example = "1", required = true)
            @RequestParam("mascotaId") Long mascotaId,
            @Parameter(description = "Contexto de la imagen", example = "PERFIL")
            @RequestParam(value = "contexto", defaultValue = "PERFIL") String contexto,
            @Parameter(description = "ID del reporte asociado", example = "10")
            @RequestParam(value = "reporteId", required = false) Long reporteId,
            @Parameter(description = "Indica si es la imagen principal", example = "false")
            @RequestParam(value = "esPrincipal", defaultValue = "false") Boolean esPrincipal) {
        
        try {
            ImagenMascota nuevaImagen = imagenService.guardarImagen(archivo, mascotaId, contexto, reporteId, esPrincipal);
            return new ResponseEntity<>(nuevaImagen, HttpStatus.CREATED);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen en el servidor: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error procesando la solicitud: " + e.getMessage());
        }
    }

}
