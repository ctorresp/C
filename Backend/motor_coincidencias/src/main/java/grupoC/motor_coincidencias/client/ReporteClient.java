package grupoC.motor_coincidencias.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import grupoC.motor_coincidencias.dto.ReporteExternoDto;

@FeignClient(name = "reporte-service", url = "${MASCOTAS_API_URL:http://localhost:8081}", configuration = grupoC.motor_coincidencias.config.FeignClientAuthConfig.class)
public interface ReporteClient {

    @GetMapping("/reportes")
    List<ReporteExternoDto> obtenerTodos();

    @GetMapping("/reportes/{id}")
    ReporteExternoDto obtenerPorId(@PathVariable("id") Long id);

    @DeleteMapping("/reportes/{id}")
    void eliminarReporte(@PathVariable("id") Long id);

}