package grupoC.motor_coincidencias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de reporte recibido desde el microservicio de mascotas")
public class ReporteExternoDto {

    @Schema(description = "ID del reporte", example = "100")
    private Long id;
    @Schema(description = "ID de la mascota asociada", example = "1")
    private Long mascotaId;
    @Schema(description = "Estado del reporte", example = "PERDIDA")
    private String estado;

}