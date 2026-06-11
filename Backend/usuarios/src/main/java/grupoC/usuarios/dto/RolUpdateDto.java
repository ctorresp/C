package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RolUpdateDto(
    @Schema(description = "Nuevo rol del usuario", allowableValues = {"CIUDADANO", "ADMINISTRADOR", "ENTIDAD"}, example = "ADMINISTRADOR")
    String nuevoRol
) {}
