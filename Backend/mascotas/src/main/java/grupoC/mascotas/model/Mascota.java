package grupoC.mascotas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad de mascota registrada en el sistema")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la mascota", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "UUID del usuario propietario", example = "550e8400-e29b-41d4-a716-446655440000")
    private String usuarioUuid;

    @Column(nullable = false)
    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Especie", example = "Perro")
    private String especie;

    @Column(nullable = false)
    @Schema(description = "Raza", example = "Labrador")
    private String raza;

    @Column(nullable = false)
    @Schema(description = "Edad", example = "3")
    private Integer edad;

    @Column(nullable = false)
    @Schema(description = "Sexo", example = "Macho")
    private String sexo;

    @Column(nullable = false)
    @Schema(description = "Color", example = "Marron")
    private String color;

    @Column(nullable = false)
    @Schema(description = "Descripción adicional", example = "Tiene collar rojo")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado de la mascota")
    private Estado estado;

}
