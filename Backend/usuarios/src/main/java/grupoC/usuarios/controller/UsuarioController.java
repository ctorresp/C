package grupoC.usuarios.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import grupoC.usuarios.dto.LoginDto;
import grupoC.usuarios.dto.LoginResponseDto;
import grupoC.usuarios.dto.RegistroDto;
import grupoC.usuarios.dto.RolUpdateDto;
import grupoC.usuarios.dto.UsuarioResponseDto;
import grupoC.usuarios.model.Usuario;
import grupoC.usuarios.security.JwtTokenProvider;
import grupoC.usuarios.service.UsuarioService;
import grupoC.usuarios.dto.RecuperarPasswordDto;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioController(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody RegistroDto registroDto){

        Usuario nuevoUsuario = usuarioService.registrarUsuario(registroDto);

        UsuarioResponseDto responseDto = mapToUsuarioResponseDto(nuevoUsuario);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUsuario(@Valid @RequestBody LoginDto loginDto){

        Usuario usuario = usuarioService.loginUsuario(loginDto);

        String token = jwtTokenProvider.generateToken(usuario);

        UsuarioResponseDto usuarioDto = mapToUsuarioResponseDto(usuario);
        LoginResponseDto loginResponse = new LoginResponseDto(token, usuarioDto);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Map<String, String>> recuperarPassword(@Valid @RequestBody RecuperarPasswordDto recuperarDto) {
        
        usuarioService.procesarRecuperacionPassword(recuperarDto.email());

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Si el correo está registrado, se han enviado las instrucciones."));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UsuarioResponseDto> getUsuarioByUuid(@PathVariable String uuid){

        Usuario usuario = usuarioService.findByUuid(uuid);
        return ResponseEntity.ok(mapToUsuarioResponseDto(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDto>> getAllUsuarios(@ParameterObject Pageable pageable){

        Page<Usuario> paginaUsuarios = usuarioService.findAll(pageable);

        Page<UsuarioResponseDto> paginaDto = paginaUsuarios.map(this::mapToUsuarioResponseDto);
        return ResponseEntity.ok(paginaDto);
    }

    @PatchMapping("/{uuid}/rol")
    public ResponseEntity<Usuario> actualizarRol(
        @PathVariable String uuid,
        @RequestBody RolUpdateDto dto
    ){
        Usuario usuarioActualizado = usuarioService.cambiarRol(uuid, dto.nuevoRol());
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String uuid){

        usuarioService.eliminarUsuario(uuid);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponseDto mapToUsuarioResponseDto(Usuario usuario) {
        return new UsuarioResponseDto(
            usuario.getUuid(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getPhone(),
            usuario.getRol()
        );
    }

}
