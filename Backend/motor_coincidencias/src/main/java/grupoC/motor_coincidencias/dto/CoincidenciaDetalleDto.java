package grupoC.motor_coincidencias.dto;

import java.time.LocalDateTime;
import grupoC.motor_coincidencias.model.EstadoCoincidencia;
import lombok.Data;

@Data
public class CoincidenciaDetalleDto {
    private Long id;
    private Double porcentajeSimilitud;
    private EstadoCoincidencia estado;
    private LocalDateTime fechaCreacion;

    // Datos agregados del Reporte Perdido
    private Long reportePerdidoId;
    private MascotaExternoDto mascotaPerdida;
    private UbicacionExternoDto ubicacionPerdido;

    // Datos agregados del Reporte Encontrado
    private Long reporteEncontradoId;
    private MascotaExternoDto mascotaEncontrada;
    private UbicacionExternoDto ubicacionEncontrado;
}