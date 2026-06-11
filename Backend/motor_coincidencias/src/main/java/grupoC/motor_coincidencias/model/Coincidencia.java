package grupoC.motor_coincidencias.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coincidencias")
@Data
@Schema(description = "Entidad persistida de coincidencia entre reporte perdido y encontrado")
public class Coincidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de coincidencia", example = "1")
    private Long id;

    @Schema(description = "ID del reporte perdido", example = "100")
    private Long reportePerdidoId;
    @Schema(description = "ID del reporte encontrado", example = "200")
    private Long reporteEncontradoId;

    @Schema(description = "Porcentaje de similitud", example = "87.5")
    private Double porcentajeSimilitud;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Estado de la coincidencia")
    private EstadoCoincidencia estado;

    @Schema(description = "Fecha de creación", example = "2026-06-11T13:45:00")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoCoincidencia.PENDIENTE;
        }
    }

}
