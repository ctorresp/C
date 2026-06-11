package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginDto(

    @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.com")
    String email,
    @Schema(description = "Contraseña en texto plano", example = "ClaveSegura123")
    String password
) {}
