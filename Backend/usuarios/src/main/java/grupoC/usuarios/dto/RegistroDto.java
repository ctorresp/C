package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegistroDto(
    @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
    String nombre,
    @Schema(description = "Correo electrónico único", example = "juan.perez@correo.com")
    String email,
    @Schema(description = "Número de teléfono de contacto", example = "+5491122334455")
    String phone,
    @Schema(description = "Contraseña inicial", example = "ClaveSegura123")
    String password
) {}
