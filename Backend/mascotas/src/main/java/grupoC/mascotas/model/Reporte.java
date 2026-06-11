package grupoC.mascotas.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad de reporte de perdida/encontrada")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del reporte", example = "10")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "ID de la mascota asociada", example = "1")
    private Long mascotaId;

    @Column(nullable = false)
    @Schema(description = "UUID del usuario que crea el reporte", example = "550e8400-e29b-41d4-a716-446655440000")
    private String usuarioUuid;

    @Column(nullable = false)
    @Schema(description = "Tipo de reporte", example = "AVISTAMIENTO")
    private String tipoReporte;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del suceso", example = "2026-06-11T12:30:00")
    LocalDateTime fechaSuceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado reportado")
    private Estado estado = Estado.PERDIDA;

}
