package grupoC.mascotas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad de imagen asociada a una mascota")
public class ImagenMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la imagen", example = "100")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "ID de la mascota", example = "1")
    private Long mascotaId;

    @Column(nullable = false)
    @Schema(description = "URL pública de la imagen", example = "http://localhost:8081/uploads/abcd.jpg")
    private String url;

    @Column(nullable =  false)
    @Schema(description = "Contexto de uso", example = "PERFIL")
    private String contexto;

    @Column(nullable = true)
    @Schema(description = "ID del reporte vinculado cuando aplica", example = "10")
    private Long reporteId;

    @Column(nullable = false)
    @Schema(description = "Indica si es la imagen principal", example = "true")
    private boolean esPrincipal;

}
