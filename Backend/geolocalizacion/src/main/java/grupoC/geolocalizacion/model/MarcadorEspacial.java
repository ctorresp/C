package grupoC.geolocalizacion.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "marcador_espacial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad persistente de marcador espacial")
public class MarcadorEspacial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del marcador", example = "1")
    private Long id;

    @Column(name = "reporte_id", nullable = false)
    @Schema(description = "ID del reporte asociado", example = "10")
    private Long reporteId;

    @Column(name = "tipo_marcador", nullable = false)
    @Schema(description = "Tipo de marcador", example = "PERDIDA")
    private String tipoMarcador;

    @Column(name = "latitud", nullable = false)
    @Schema(description = "Latitud real", example = "-34.6037")
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    @Schema(description = "Longitud real", example = "-58.3816")
    private Double longitud;

    @Column(name = "fecha_hora_registro", nullable = false)
    @Schema(description = "Fecha y hora de registro", example = "2026-06-11T12:30:00")
    private LocalDateTime fechaHoraRegistro;
}
