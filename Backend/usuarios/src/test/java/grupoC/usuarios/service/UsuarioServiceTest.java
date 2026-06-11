package grupoC.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import grupoC.usuarios.dto.LoginDto;
import grupoC.usuarios.dto.RegistroDto;
import grupoC.usuarios.exception.EmailYaExisteException;
import grupoC.usuarios.exception.UsuarioNotFoundException;
import grupoC.usuarios.model.Rol;
import grupoC.usuarios.model.Usuario;
import grupoC.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private UsuarioService usuarioService;

	private Usuario usuario;

	@BeforeEach
	void setUp() {
		usuario = new Usuario();
		usuario.setUuid("uuid-123");
		usuario.setNombre("Ana");
		usuario.setEmail("ana@example.com");
		usuario.setPhone("555123");
		usuario.setRol(Rol.CIUDADANO);
		usuario.setPasswordHash("hash-original");
	}

	// Verifica que el servicio delega la paginación en el repositorio.
	@Test
	void findAll_debeRetornarLaPaginaDelRepositorio() {
		PageRequest pageable = PageRequest.of(0, 10);
		Page<Usuario> pagina = new PageImpl<>(List.of(usuario));

		when(usuarioRepository.findAll(pageable)).thenReturn(pagina);

		Page<Usuario> resultado = usuarioService.findAll(pageable);

		assertSame(pagina, resultado);
		verify(usuarioRepository).findAll(pageable);
	}

	// Verifica que la búsqueda por UUID devuelve el usuario cuando existe.
	@Test
	void findByUuid_debeRetornarUsuarioCuandoExiste() {
		when(usuarioRepository.findByUuid("uuid-123")).thenReturn(Optional.of(usuario));

		Usuario resultado = usuarioService.findByUuid("uuid-123");

		assertSame(usuario, resultado);
		verify(usuarioRepository).findByUuid("uuid-123");
	}

	// Verifica que la búsqueda por UUID falla cuando el usuario no existe.
	@Test
	void findByUuid_debeLanzarExcepcionCuandoNoExiste() {
		when(usuarioRepository.findByUuid("uuid-inexistente")).thenReturn(Optional.empty());

		UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
				() -> usuarioService.findByUuid("uuid-inexistente"));

		assertEquals("Usuario con UUID uuid-inexistente no encontrado", exception.getMessage());
	}

	// Verifica que el servicio consulta correctamente si existe un email.
	@Test
	void existePorEmail_debeConsultarRepositorio() {
		when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);

		boolean resultado = usuarioService.existePorEmail("ana@example.com");

		assertTrue(resultado);
		verify(usuarioRepository).existsByEmail("ana@example.com");
	}

	// Verifica que el registro asigna rol, hashea la clave y persiste al usuario.
	@Test
	void registrarUsuario_debeHashearPasswordYGuardarUsuarioConRolCiudadano() {
		RegistroDto registroDto = new RegistroDto("Ana", "ana@example.com", "555123", "secreta");
		when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
		when(passwordEncoder.encode("secreta")).thenReturn("hash-123");
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Usuario resultado = usuarioService.registrarUsuario(registroDto);

		assertEquals("Ana", resultado.getNombre());
		assertEquals("ana@example.com", resultado.getEmail());
		assertEquals("555123", resultado.getPhone());
		assertEquals("hash-123", resultado.getPasswordHash());
		assertEquals(Rol.CIUDADANO, resultado.getRol());
		verify(passwordEncoder).encode("secreta");
		verify(usuarioRepository).save(any(Usuario.class));
	}

	// Verifica que no se registra un usuario cuando el email ya está usado.
	@Test
	void registrarUsuario_debeLanzarExcepcionSiEmailYaExiste() {
		RegistroDto registroDto = new RegistroDto("Ana", "ana@example.com", "555123", "secreta");
		when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);

		EmailYaExisteException exception = assertThrows(EmailYaExisteException.class,
				() -> usuarioService.registrarUsuario(registroDto));

		assertEquals("El email ya está registradoana@example.com", exception.getMessage());
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	// Verifica que el login devuelve el usuario cuando la contraseña coincide.
	@Test
	void loginUsuario_debeRetornarUsuarioCuandoCredencialesSonValidas() {
		LoginDto loginDto = new LoginDto("ana@example.com", "secreta");
		when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches("secreta", "hash-original")).thenReturn(true);

		Usuario resultado = usuarioService.loginUsuario(loginDto);

		assertSame(usuario, resultado);
		verify(passwordEncoder).matches("secreta", "hash-original");
	}

	// Verifica que el login falla cuando el email no está registrado.
	@Test
	void loginUsuario_debeLanzarExcepcionSiEmailNoExiste() {
		LoginDto loginDto = new LoginDto("ana@example.com", "secreta");
		when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.empty());

		BadCredentialsException exception = assertThrows(BadCredentialsException.class,
				() -> usuarioService.loginUsuario(loginDto));

		assertEquals("Email o contraseña incorrectos", exception.getMessage());
	}

	// Verifica que el login falla cuando la contraseña no coincide con el hash.
	@Test
	void loginUsuario_debeLanzarExcepcionSiPasswordNoCoincide() {
		LoginDto loginDto = new LoginDto("ana@example.com", "incorrecta");
		when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches("incorrecta", "hash-original")).thenReturn(false);

		BadCredentialsException exception = assertThrows(BadCredentialsException.class,
				() -> usuarioService.loginUsuario(loginDto));

		assertEquals("Email o contraseña incorrectos", exception.getMessage());
	}

	// Verifica que el cambio de rol actualiza y persiste el rol correcto.
	@Test
	void cambiarRol_debeActualizarRolCuandoUuidExiste() {
		when(usuarioRepository.findByUuid("uuid-123")).thenReturn(Optional.of(usuario));
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Usuario resultado = usuarioService.cambiarRol("uuid-123", "adminISTRADOR");

		assertEquals(Rol.ADMINISTRADOR, resultado.getRol());
		verify(usuarioRepository).save(usuario);
	}

	// Verifica que el cambio de rol falla si el UUID no existe.
	@Test
	void cambiarRol_debeLanzarExcepcionSiUuidNoExiste() {
		when(usuarioRepository.findByUuid("uuid-inexistente")).thenReturn(Optional.empty());

		UsuarioNotFoundException exception = assertThrows(UsuarioNotFoundException.class,
				() -> usuarioService.cambiarRol("uuid-inexistente", "ADMINISTRADOR"));

		assertEquals("Usuario con UUID uuid-inexistente no encontrado", exception.getMessage());
	}

	// Verifica que el cambio de rol rechaza valores que no pertenecen al enum.
	@Test
	void cambiarRol_debeLanzarExcepcionSiRolEsInvalido() {
		when(usuarioRepository.findByUuid("uuid-123")).thenReturn(Optional.of(usuario));

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> usuarioService.cambiarRol("uuid-123", "ROL_INEXISTENTE"));

		assertEquals("Rol no válido", exception.getMessage());
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	// Verifica que la recuperación de contraseña ignora correos no registrados.
	@Test
	void procesarRecuperacionPassword_debeIgnorarCorreoNoRegistrado() {
		when(usuarioRepository.findByEmail("desconocido@example.com")).thenReturn(Optional.empty());

		usuarioService.procesarRecuperacionPassword("desconocido@example.com");

		verifyNoInteractions(passwordEncoder, mailSender);
		verify(usuarioRepository, never()).save(any(Usuario.class));
	}

	// Verifica que la recuperación de contraseña actualiza el hash y envía el correo.
	@Test
	void procesarRecuperacionPassword_debeActualizarPasswordYEnviarCorreo() {
		when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "hash-" + invocation.getArgument(0));
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

		usuarioService.procesarRecuperacionPassword("ana@example.com");

		verify(passwordEncoder).encode(passwordCaptor.capture());
		verify(usuarioRepository).save(any(Usuario.class));
		verify(mailSender).send(messageCaptor.capture());

		String claveTemporal = passwordCaptor.getValue();
		assertEquals(8, claveTemporal.length());
		assertEquals("hash-" + claveTemporal, usuario.getPasswordHash());
		assertEquals("tu_correo_real@gmail.com", messageCaptor.getValue().getFrom());
		assertEquals("ana@example.com", messageCaptor.getValue().getTo()[0]);
		assertEquals("Recuperación de Contraseña - Sanos y Salvos", messageCaptor.getValue().getSubject());
		assertTrue(messageCaptor.getValue().getText().contains(claveTemporal));
		assertTrue(messageCaptor.getValue().getText().contains("Ana"));
	}

	// Verifica que eliminar usuario delega correctamente en el repositorio.
	@Test
	void eliminarUsuario_debeDelegarEnRepositorio() {
		usuarioService.eliminarUsuario("uuid-123");

		verify(usuarioRepository).deleteByUuid("uuid-123");
	}
}
