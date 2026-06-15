package grupoC.geolocalizacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import grupoC.geolocalizacion.model.MarcadorEspacial;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface MarcadorEspacialRepository extends JpaRepository<MarcadorEspacial, Long> {

    Optional<MarcadorEspacial> findByReporteId(Long reporteId);

    @Transactional
    void deleteByReporteId(Long reporteId);

    @Transactional
    void deleteByReporteIdIn(List<Long> reporteIds);

}
