package grupoC.mascotas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import grupoC.mascotas.client.GeolocalizacionClient;
import grupoC.mascotas.dto.MascotaRequestDTO;
import grupoC.mascotas.dto.MascotaResponseDTO;
import grupoC.mascotas.model.ImagenMascota;
import grupoC.mascotas.model.Mascota;
import grupoC.mascotas.model.Reporte;
import grupoC.mascotas.repository.ImagenMascotaRepository;
import grupoC.mascotas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    
    private final ImagenMascotaRepository imagenRepository;

    private final GeolocalizacionClient geolocalizacionClient;

    public MascotaResponseDTO registrarMascota(MascotaRequestDTO dto, String usuarioUuid) {
        Mascota mascota = new Mascota();
        mascota.setNombre(dto.nombre());
        mascota.setEspecie(dto.especie());
        mascota.setRaza(dto.raza());
        mascota.setEdad(dto.edad());
        mascota.setSexo(dto.sexo());
        mascota.setColor(dto.color());
        mascota.setDescripcion(dto.descripcion());
        mascota.setEstado(dto.estado());
        mascota.setUsuarioUuid(usuarioUuid);
        
        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return mapToDto(mascotaGuardada);
    }

    public List<MascotaResponseDTO> obtenerTodas(){
        return mascotaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<MascotaResponseDTO> obtenerPorUsuario(String usuarioUuid) {
        return mascotaRepository.findAllByUsuarioUuid(usuarioUuid)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MascotaResponseDTO obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));
        return mapToDto(mascota);
    }

    public MascotaResponseDTO actualizarMascota(Long id, MascotaRequestDTO dto) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));

        mascota.setNombre(dto.nombre());
        mascota.setEspecie(dto.especie());
        mascota.setRaza(dto.raza());
        mascota.setEdad(dto.edad());
        mascota.setSexo(dto.sexo());
        mascota.setColor(dto.color());
        mascota.setDescripcion(dto.descripcion());
        mascota.setEstado(dto.estado());

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return mapToDto(mascotaActualizada);
    }

    public void eliminarMascota(Long id) {
        // En lugar de usar existsById, recuperamos la mascota con sus reportes cargados
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));

        // 1. Extraemos todos los IDs de los reportes asociados a esta mascota antes de borrarla
        List<Long> reporteIds = mascota.getReportes().stream()
                .map(Reporte::getId)
                .collect(Collectors.toList());

        // 2. Si tenía reportes activos, disparamos la petición Feign en lote para limpiar el mapa
        if (!reporteIds.isEmpty()) {
            try {
                geolocalizacionClient.eliminarMarcadoresEnLote(reporteIds);
            } catch (Exception e) {
                System.err.println("Error eliminando marcadores en lote: " + e.getMessage());
            }
        }

        // 3. Eliminamos la mascota. 
        // El cascade = CascadeType.ALL se encargará de borrar los reportes de la tabla local.
        mascotaRepository.delete(mascota);
    }

    private MascotaResponseDTO mapToDto(Mascota mascota) {
        
        List<ImagenMascota> imagenes = imagenRepository.findByMascotaId(mascota.getId());
        String urlFoto = null;

        if (!imagenes.isEmpty()) {
            urlFoto = imagenes.stream()
                    .filter(ImagenMascota::isEsPrincipal)
                    .findFirst()
                    .orElse(imagenes.get(0))
                    .getUrl();
        }

        return new MascotaResponseDTO(
                mascota.getId(),
                mascota.getUsuarioUuid(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getEdad(),
                mascota.getSexo(),
                mascota.getColor(),
                mascota.getDescripcion(),
                mascota.getEstado(),
                urlFoto
        );
    }
}