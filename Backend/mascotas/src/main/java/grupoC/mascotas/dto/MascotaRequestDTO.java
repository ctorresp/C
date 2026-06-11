package grupoC.mascotas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.mascotas.model.Estado;

public record MascotaRequestDTO(
        @Schema(description = "Nombre de la mascota", example = "Firulais")
        String nombre,
        @Schema(description = "Especie de la mascota", example = "Perro")
        String especie,
        @Schema(description = "Raza de la mascota", example = "Labrador")
        String raza,
        @Schema(description = "Edad en años", example = "3")
        int edad,
        @Schema(description = "Sexo de la mascota", example = "Macho")
        String sexo,
        @Schema(description = "Color predominante", example = "Marron")
        String color,
        @Schema(description = "Descripción adicional", example = "Tiene collar rojo")
        String descripcion,
        @Schema(description = "Estado del caso", example = "PERDIDA")
        Estado estado
) {}
