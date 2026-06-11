package grupoC.geolocalizacion.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record MarcadorResponseDTO(
    @Schema(description = "ID del marcador", example = "1")
    Long id,
    @Schema(description = "ID del reporte asociado", example = "10")
    Long reporteId,
    @Schema(description = "Tipo de marcador", example = "PERDIDA")
    String tipoMarcador,
    @Schema(description = "Latitud ofuscada para visualización pública", example = "-34.6021")
    Double latitudOfuscada,
    @Schema(description = "Longitud ofuscada para visualización pública", example = "-58.3801")
    Double longitudOfuscada,
    @Schema(description = "Fecha y hora de registro", example = "2026-06-11T12:30:00")
    LocalDateTime fechaHoraRegistro
) {}
