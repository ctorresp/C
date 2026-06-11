package grupoC.usuarios.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import grupoC.usuarios.dto.LoginDto;
import grupoC.usuarios.dto.LoginResponseDto;
import grupoC.usuarios.dto.RegistroDto;
import grupoC.usuarios.dto.RecuperarPasswordDto;
import grupoC.usuarios.dto.RolUpdateDto;
import grupoC.usuarios.dto.UsuarioResponseDto;
import grupoC.usuarios.model.Rol;
import grupoC.usuarios.model.Usuario;
import grupoC.usuarios.security.JwtTokenProvider;
import grupoC.usuarios.service.UsuarioService;

public class UsuarioControllerTest {

	@Mock
	private UsuarioService usuarioService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	private UsuarioController usuarioController;

	private AutoCloseable mocks;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);
		usuarioController = new UsuarioController(usuarioService, jwtTokenProvider);
	}

	// Verifica que el registro responde con 201 y mapea correctamente el usuario.
	@Test
	void registrarUsuario_debeRetornar201YMapearUsuario() {
		RegistroDto registroDto = new RegistroDto("Ana", "ana@example.com", "555123", "secreta");
		Usuario usuario = usuario("uuid-123", "Ana", "ana@example.com", "555123", Rol.CIUDADANO, "hash");
		when(usuarioService.registrarUsuario(registroDto)).thenReturn(usuario);

		ResponseEntity<UsuarioResponseDto> response = usuarioController.registrarUsuario(registroDto);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("uuid-123", response.getBody().uuid());
		assertEquals("Ana", response.getBody().nombre());
		assertEquals("ana@example.com", response.getBody().email());
		assertEquals("555123", response.getBody().phone());
		assertEquals(Rol.CIUDADANO, response.getBody().rol());
	}

	// Verifica que el login responde con token y usuario mapeado.
	@Test
	void loginUsuario_debeRetornarTokenYUsuario() {
		LoginDto loginDto = new LoginDto("ana@example.com", "secreta");
		Usuario usuario = usuario("uuid-123", "Ana", "ana@example.com", "555123", Rol.ADMINISTRADOR, "hash");
		when(usuarioService.loginUsuario(loginDto)).thenReturn(usuario);
		when(jwtTokenProvider.generateToken(usuario)).thenReturn("token-123");

		ResponseEntity<LoginResponseDto> response = usuarioController.loginUsuario(loginDto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("token-123", response.getBody().token());
		assertEquals("uuid-123", response.getBody().usuario().uuid());
		assertEquals(Rol.ADMINISTRADOR, response.getBody().usuario().rol());
		verify(jwtTokenProvider).generateToken(usuario);
	}

	// Verifica que recuperar contraseña devuelve el mensaje estándar y delega en el servicio.
	@Test
	void recuperarPassword_debeResponderMensajeEInvocarServicio() {
		RecuperarPasswordDto recuperarDto = new RecuperarPasswordDto("ana@example.com");

		ResponseEntity<Map<String, String>> response = usuarioController.recuperarPassword(recuperarDto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Si el correo está registrado, se han enviado las instrucciones.", response.getBody().get("mensaje"));
		verify(usuarioService).procesarRecuperacionPassword("ana@example.com");
	}

	// Verifica que la búsqueda por UUID devuelve el usuario mapeado al DTO.
	@Test
	void getUsuarioByUuid_debeRetornar200ConUsuarioMapeado() {
		Usuario usuario = usuario("uuid-123", "Ana", "ana@example.com", "555123", Rol.ENTIDAD, "hash");
		when(usuarioService.findByUuid("uuid-123")).thenReturn(usuario);

		ResponseEntity<UsuarioResponseDto> response = usuarioController.getUsuarioByUuid("uuid-123");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("uuid-123", response.getBody().uuid());
		assertEquals(Rol.ENTIDAD, response.getBody().rol());
	}

	// Verifica que la consulta paginada transforma cada usuario al DTO correcto.
	@Test
	void getAllUsuarios_debeMapearPaginaDeUsuarios() {
		Usuario usuario1 = usuario("uuid-1", "Ana", "ana@example.com", "555123", Rol.CIUDADANO, "hash");
		Usuario usuario2 = usuario("uuid-2", "Luis", "luis@example.com", "555456", Rol.ADMINISTRADOR, "hash2");
		Page<Usuario> paginaUsuarios = new PageImpl<>(List.of(usuario1, usuario2));
		when(usuarioService.findAll(PageRequest.of(0, 20))).thenReturn(paginaUsuarios);

		ResponseEntity<Page<UsuarioResponseDto>> response = usuarioController.getAllUsuarios(PageRequest.of(0, 20));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().getContent().size());
		assertEquals("Ana", response.getBody().getContent().get(0).nombre());
		assertEquals("Luis", response.getBody().getContent().get(1).nombre());
	}

	// Verifica que el cambio de rol retorna el usuario actualizado.
	@Test
	void actualizarRol_debeRetornarUsuarioActualizado() {
		Usuario usuario = usuario("uuid-123", "Ana", "ana@example.com", "555123", Rol.ADMINISTRADOR, "hash");
		RolUpdateDto dto = new RolUpdateDto("ADMINISTRADOR");
		when(usuarioService.cambiarRol("uuid-123", "ADMINISTRADOR")).thenReturn(usuario);

		ResponseEntity<Usuario> response = usuarioController.actualizarRol("uuid-123", dto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(usuario, response.getBody());
		verify(usuarioService).cambiarRol("uuid-123", "ADMINISTRADOR");
	}

	// Verifica que eliminar usuario responde 204 y delega la eliminación.
	@Test
	void eliminarUsuario_debeResponder204YDelegarEnServicio() {
		ResponseEntity<Void> response = usuarioController.eliminarUsuario("uuid-123");

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(usuarioService).eliminarUsuario("uuid-123");
	}

	private Usuario usuario(String uuid, String nombre, String email, String phone, Rol rol, String passwordHash) {
		Usuario usuario = new Usuario();
		usuario.setUuid(uuid);
		usuario.setNombre(nombre);
		usuario.setEmail(email);
		usuario.setPhone(phone);
		usuario.setRol(rol);
		usuario.setPasswordHash(passwordHash);
		return usuario;
	}
}
