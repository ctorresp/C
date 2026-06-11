package grupoC.mascotas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.mascotas.model.Estado;

public record ReporteRequestDTO(
    @Schema(description = "ID de la mascota asociada al reporte", example = "1")
    Long mascotaId,
    @Schema(description = "Tipo de reporte", example = "AVISTAMIENTO")
    String tipoReporte,
    @Schema(description = "Estado de la mascota", example = "PERDIDA")
    Estado estado
) {

}
