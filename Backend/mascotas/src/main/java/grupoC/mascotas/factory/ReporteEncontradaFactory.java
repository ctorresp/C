package grupoC.mascotas.factory;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import grupoC.mascotas.dto.ReporteRequestDTO;
import grupoC.mascotas.model.Estado;
import grupoC.mascotas.model.Reporte;

@Component
public class ReporteEncontradaFactory implements ReporteFactoryStrategy {

    @Override
    public Reporte armarReporte(ReporteRequestDTO dto, String usuarioUuid) {
        Reporte reporte = new Reporte();
        reporte.setUsuarioUuid(usuarioUuid);
        reporte.setEstado(Estado.ENCONTRADA);
        reporte.setFechaSuceso(LocalDateTime.now());
        
        reporte.setTipoReporte(dto.tipoReporte() != null ? dto.tipoReporte() : "Aviso de Mascota Encontrada");
        
        return reporte;
    }

    @Override
    public Estado getEstadoSoportado() {
        return Estado.ENCONTRADA;
    }

}
