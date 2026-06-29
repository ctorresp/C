package grupoC.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operaciones de registro, autenticación y administración de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioController(UsuarioService usuarioService, JwtTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con rol inicial CIUDADANO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody RegistroDto registroDto){

        Usuario nuevoUsuario = usuarioService.registrarUsuario(registroDto);

        UsuarioResponseDto responseDto = mapToUsuarioResponseDto(nuevoUsuario);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponseDto> loginUsuario(@Valid @RequestBody LoginDto loginDto){

        Usuario usuario = usuarioService.loginUsuario(loginDto);

        String token = jwtTokenProvider.generateToken(usuario);

        UsuarioResponseDto usuarioDto = mapToUsuarioResponseDto(usuario);
        LoginResponseDto loginResponse = new LoginResponseDto(token, usuarioDto);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/recuperar-password")
    @Operation(summary = "Recuperar contraseña", description = "Genera una contraseña temporal y envía un correo si la cuenta existe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud procesada correctamente"),
        @ApiResponse(responseCode = "400", description = "Formato de correo inválido")
    })
    public ResponseEntity<Map<String, String>> recuperarPassword(@Valid @RequestBody RecuperarPasswordDto recuperarDto) {
        
        usuarioService.procesarRecuperacionPassword(recuperarDto.email());

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Si el correo está registrado, se han enviado las instrucciones."));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Obtener usuario por UUID", description = "Devuelve los datos públicos del usuario")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UsuarioResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioResponseDto> getUsuarioByUuid(@PathVariable String uuid){

        Usuario usuario = usuarioService.findByUuid(uuid);
        return ResponseEntity.ok(mapToUsuarioResponseDto(usuario));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Devuelve una lista paginada de usuarios")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado paginado obtenido correctamente")
    })
    public ResponseEntity<Page<UsuarioResponseDto>> getAllUsuarios(@ParameterObject Pageable pageable){

        Page<Usuario> paginaUsuarios = usuarioService.findAll(pageable);

        Page<UsuarioResponseDto> paginaDto = paginaUsuarios.map(this::mapToUsuarioResponseDto);
        return ResponseEntity.ok(paginaDto);
    }

    @PatchMapping("/{uuid}/rol")
    @Operation(summary = "Actualizar rol", description = "Cambia el rol del usuario indicado por UUID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Rol no válido")
    })
    public ResponseEntity<Usuario> actualizarRol(
        @Parameter(description = "UUID del usuario", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String uuid,
        @RequestBody RolUpdateDto dto
    ){
        Usuario usuarioActualizado = usuarioService.cambiarRol(uuid, dto.nuevoRol());
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PutMapping("/{uuid}/password")
    @Operation(summary = "Actualizar contraseña", description = "Permite a un usuario cambiar su contraseña actual validando la anterior")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contraseña actualizada correctamente"),
        @ApiResponse(responseCode = "401", description = "La contraseña actual es incorrecta"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> actualizarContrasena(
            @Parameter(description = "UUID del usuario", example = "550e8400-e29b-41d4-a716-446655440000") 
            @PathVariable String uuid,
            @Valid @RequestBody grupoC.usuarios.dto.CambioPasswordDto dto
    ) {
        usuarioService.actualizarContrasena(uuid, dto);
        
        return ResponseEntity.noContent().build(); 
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Eliminar usuario", description = "Elimina el usuario identificado por UUID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente")
    })
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
