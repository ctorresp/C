package grupoC.motor_coincidencias.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import grupoC.motor_coincidencias.dto.MascotaExternoDto;

@FeignClient(name = "mascota-service", url = "${MASCOTAS_API_URL:http://localhost:8081}", configuration = grupoC.motor_coincidencias.config.FeignClientAuthConfig.class)
public interface MascotaClient {

    @GetMapping("/mascotas")
    List<MascotaExternoDto> obtenerTodas();

    @GetMapping("/mascotas/{id}")
    MascotaExternoDto obtenerPorId(@PathVariable("id") Long id);

    @PutMapping("/mascotas/{id}/estado")
    void actualizarEstadoMascota(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestParam("estado") String estado);

}
