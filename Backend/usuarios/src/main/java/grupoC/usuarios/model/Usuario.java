package grupoC.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "usuarios")
@Schema(description = "Entidad de usuario del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Identificador interno autogenerado", example = "1")
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    @Schema(description = "Identificador único público", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre completo", example = "Juan Perez")
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Correo electrónico único", example = "juan.perez@correo.com")
    private String email;

    @Column(name = "phone", nullable = true)
    @Schema(description = "Número de teléfono", example = "+5491122334455")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    @Schema(description = "Rol del usuario")
    private Rol rol;

    @Column(name = "password_hash", nullable = false)
    @Schema(description = "Hash de contraseña", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Schema(description = "Fecha de creación", example = "2026-06-11T14:30:00")
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    @Schema(description = "Fecha de última actualización", example = "2026-06-11T15:00:00")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
