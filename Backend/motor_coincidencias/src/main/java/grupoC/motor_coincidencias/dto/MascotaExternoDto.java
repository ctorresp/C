package grupoC.motor_coincidencias.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de mascota recibido desde el microservicio de mascotas")
public class MascotaExternoDto {

    @Schema(description = "ID de mascota", example = "1")
    private Long id;
    @Schema(description = "ID de reporte asociado", example = "100")
    private Long reporteId;
    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;
    @Schema(description = "Especie", example = "Perro")
    private String especie;
    @Schema(description = "Raza", example = "Labrador")
    private String raza;
    @Schema(description = "Edad", example = "3")
    private Integer edad;
    @Schema(description = "Sexo", example = "Macho")
    private String sexo;
    @Schema(description = "Color", example = "Marron")
    private String color;
    @Schema(description = "Estado declarado", example = "PERDIDA")
    private String estado;

}
