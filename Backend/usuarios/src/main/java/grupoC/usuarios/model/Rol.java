package grupoC.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Roles disponibles en la plataforma")
public enum Rol {

    @Schema(description = "Usuario ciudadano")
    CIUDADANO,
    @Schema(description = "Administrador del sistema")
    ADMINISTRADOR,
    @Schema(description = "Entidad u organización")
    ENTIDAD

}
