package grupoC.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarPasswordDto(

    @Schema(description = "Correo de la cuenta que solicita recuperación", example = "usuario@correo.com")
    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    String email
) {

}
