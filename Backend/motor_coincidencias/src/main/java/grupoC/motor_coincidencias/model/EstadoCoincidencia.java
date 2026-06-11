package grupoC.motor_coincidencias.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de una coincidencia")
public enum EstadoCoincidencia {

    @Schema(description = "Coincidencia detectada pero no revisada")
    PENDIENTE, //El motor encontró la coincidencia pero el usuario aún no la ha revisado
    @Schema(description = "Coincidencia revisada")
    REVISADA, //El usuario ha revisado la coincidencia pero aún no ha tomado una decisión
    @Schema(description = "Coincidencia descartada")
    DESCARTADA, //El usuario ha descartado la coincidencia
    @Schema(description = "Coincidencia confirmada como exitosa")
    EXITOSA //La coincidencia ha sido exitosa

}
