package grupoC.geolocalizacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MarcadorRequestDTO(
    @Schema(description = "ID del reporte asociado", example = "10")
    Long reporteId,
    @Schema(description = "Tipo de marcador", example = "PERDIDA")
    String tipoMarcador,
    @Schema(description = "Latitud real registrada", example = "-34.6037")
    Double latitud,
    @Schema(description = "Longitud real registrada", example = "-58.3816")
    Double longitud
) {}
