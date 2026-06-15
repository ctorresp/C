package grupoC.mascotas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "geolocalizacion-service", url = "${GEOLOCALIZACION_API_URL:http://localhost:8082}")
public interface GeolocalizacionClient {

    @DeleteMapping("/api/marcadores/reporte/{reporteId}")
    void eliminarMarcadoresPorReporte(@PathVariable("reporteId") Long reporteId);

    @DeleteMapping("/api/marcadores/reportes/batch")
    void eliminarMarcadoresEnLote(@RequestParam("ids") List<Long> reporteIds);
}