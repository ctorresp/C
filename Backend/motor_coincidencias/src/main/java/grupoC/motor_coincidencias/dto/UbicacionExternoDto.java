package grupoC.motor_coincidencias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de ubicación ofuscada obtenido de geolocalización")
public class UbicacionExternoDto {

    @Schema(description = "ID de ubicación", example = "500")
    private Long id;
    @Schema(description = "Latitud ofuscada", example = "-34.6021")
    private Double latitudOfuscada;
    @Schema(description = "Longitud ofuscada", example = "-58.3801")
    private Double longitudOfuscada;

}
