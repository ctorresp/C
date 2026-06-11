package grupoC.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "organizaciones")
@Schema(description = "Entidad que representa una organización vinculada a un usuario")
public class Organizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador interno de organización", example = "1")
    private Long id;

    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre de la organización", example = "Refugio Patitas")
    private String nombre;

    @Column(name = "direccion", nullable = true)
    @Schema(description = "Dirección física de la organización", example = "Av. Siempre Viva 742")
    private String direccion;

    @Column(name = "sitioweb", nullable = true)
    @Schema(description = "Sitio web de la organización", example = "https://refugiopatitas.org")
    private String sitioweb;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    @Schema(description = "Usuario asociado a la organización")
    private Usuario usuario;

}
