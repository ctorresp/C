package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambioPasswordDto(

    @Schema(description = "Contraseña actual del usuario", example = "claveFuerte123")
    @NotBlank(message = "La contraseña actual no puede estar vacía")
    String currentPassword,

    @Schema(description = "Nueva contraseña deseada", example = "miNuevaClaveSegura")
    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    String newPassword

) {

}
