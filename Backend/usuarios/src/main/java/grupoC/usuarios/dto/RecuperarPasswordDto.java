package grupoC.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperarPasswordDto(

    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    String email
) {

}
