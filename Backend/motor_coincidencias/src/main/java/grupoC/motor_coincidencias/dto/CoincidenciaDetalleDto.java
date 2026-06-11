package grupoC.motor_coincidencias.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.motor_coincidencias.model.EstadoCoincidencia;
import lombok.Data;

@Data
@Schema(description = "Detalle enriquecido de una coincidencia")
public class CoincidenciaDetalleDto {
    @Schema(description = "ID de la coincidencia", example = "1")
    private Long id;
    @Schema(description = "Porcentaje de similitud", example = "87.5")
    private Double porcentajeSimilitud;
    @Schema(description = "Estado actual de la coincidencia")
    private EstadoCoincidencia estado;
    @Schema(description = "Fecha de creación", example = "2026-06-11T13:45:00")
    private LocalDateTime fechaCreacion;

    // Datos agregados del Reporte Perdido
    @Schema(description = "ID del reporte perdido", example = "100")
    private Long reportePerdidoId;
    @Schema(description = "Mascota asociada al reporte perdido")
    private MascotaExternoDto mascotaPerdida;
    @Schema(description = "Ubicación asociada al reporte perdido")
    private UbicacionExternoDto ubicacionPerdido;

    // Datos agregados del Reporte Encontrado
    @Schema(description = "ID del reporte encontrado", example = "200")
    private Long reporteEncontradoId;
    @Schema(description = "Mascota asociada al reporte encontrado")
    private MascotaExternoDto mascotaEncontrada;
    @Schema(description = "Ubicación asociada al reporte encontrado")
    private UbicacionExternoDto ubicacionEncontrado;
}