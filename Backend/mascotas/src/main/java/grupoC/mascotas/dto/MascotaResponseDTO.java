package grupoC.mascotas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.mascotas.model.Estado;

public record MascotaResponseDTO(
        @Schema(description = "ID de la mascota", example = "1")
        Long id,
        @Schema(description = "UUID del usuario propietario", example = "550e8400-e29b-41d4-a716-446655440000")
        String usuarioUuid,
        @Schema(description = "Nombre de la mascota", example = "Firulais")
        String nombre,
        @Schema(description = "Especie", example = "Perro")
        String especie,
        @Schema(description = "Raza", example = "Labrador")
        String raza,
        @Schema(description = "Edad", example = "3")
        int edad,
        @Schema(description = "Sexo", example = "Macho")
        String sexo,
        @Schema(description = "Color", example = "Marron")
        String color,
        @Schema(description = "Descripción", example = "Tiene collar rojo")
        String descripcion,
        @Schema(description = "Estado del caso", example = "PERDIDA")
        Estado estado
) {

}
