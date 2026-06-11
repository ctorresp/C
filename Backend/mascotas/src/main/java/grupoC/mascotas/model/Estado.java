package grupoC.mascotas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de una mascota en un reporte")
public enum Estado {

    @Schema(description = "Mascota reportada como encontrada")
    ENCONTRADA,
    @Schema(description = "Mascota reportada como perdida")
    PERDIDA

}
