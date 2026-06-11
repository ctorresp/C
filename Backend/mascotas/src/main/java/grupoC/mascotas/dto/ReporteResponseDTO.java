package grupoC.mascotas.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.mascotas.model.Estado;

public record ReporteResponseDTO(
    @Schema(description = "ID del reporte", example = "10")
    Long id,
    @Schema(description = "ID de la mascota asociada", example = "1")
    Long mascotaId,
    @Schema(description = "UUID del usuario creador", example = "550e8400-e29b-41d4-a716-446655440000")
    String usuarioUuid,
    @Schema(description = "Tipo de reporte", example = "AVISTAMIENTO")
    String tipoReporte,
    @Schema(description = "Fecha y hora del suceso", example = "2026-06-11T12:30:00")
    LocalDateTime fechaSuceso,
    @Schema(description = "Estado reportado", example = "ENCONTRADA")
    Estado estado
) {

}
