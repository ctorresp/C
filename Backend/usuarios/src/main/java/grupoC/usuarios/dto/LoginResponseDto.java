package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDto(
    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...")
    String token,
    @Schema(description = "Datos públicos del usuario autenticado")
    UsuarioResponseDto usuario
) {}
