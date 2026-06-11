package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import grupoC.usuarios.model.Rol;

public record UsuarioResponseDto(
    @Schema(description = "Identificador único público", example = "550e8400-e29b-41d4-a716-446655440000")
    String uuid,
    @Schema(description = "Nombre del usuario", example = "Juan Perez")
    String nombre,
    @Schema(description = "Email del usuario", example = "juan.perez@correo.com")
    String email,
    @Schema(description = "Teléfono del usuario", example = "+5491122334455")
    String phone,
    @Schema(description = "Rol actual del usuario")
    Rol rol
) {

}
