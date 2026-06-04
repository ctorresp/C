package grupoC.usuarios.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// IMPORTANTE: Nuevas importaciones para el envío de correos y generación aleatoria
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.UUID;
import java.util.Optional;

import grupoC.usuarios.dto.LoginDto;
import grupoC.usuarios.dto.RegistroDto;
import grupoC.usuarios.exception.EmailYaExisteException;
import grupoC.usuarios.exception.UsuarioNotFoundException;
import grupoC.usuarios.model.Rol;
import grupoC.usuarios.model.Usuario;
import grupoC.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // <-- Añadido para interactuar con el SMTP de Gmail

    // Actualizamos el constructor para inyectar JavaMailSender
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional(readOnly = true)
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Usuario findByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario con UUID " + uuid + " no encontrado"));
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
    public Usuario registrarUsuario(RegistroDto registroDto) {
        if (usuarioRepository.existsByEmail(registroDto.email())) {
            throw new EmailYaExisteException("El email ya está registrado" + registroDto.email());
        }

        String passwordHash = passwordEncoder.encode(registroDto.password());

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDto.nombre());
        nuevoUsuario.setEmail(registroDto.email());
        nuevoUsuario.setPhone(registroDto.phone());
        nuevoUsuario.setPasswordHash(passwordHash);
        nuevoUsuario.setRol(Rol.CIUDADANO);

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional(readOnly = true)
    public Usuario loginUsuario(LoginDto loginDto) {
        Usuario usuario = usuarioRepository.findByEmail(loginDto.email())
            .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));
        
        if(passwordEncoder.matches(loginDto.password(), usuario.getPasswordHash())){
            return usuario;
        } else {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }
    }

    public Usuario cambiarRol(String uuid, String nuevoRolStr) {
        Usuario usuario = usuarioRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario con UUID " + uuid + " no encontrado"));

        try {
            Rol rolEnum = Rol.valueOf(nuevoRolStr.toUpperCase());
            usuario.setRol(rolEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no válido");
        }
        return usuarioRepository.save(usuario);
    }

    // LÓGICA COMPLETA DE RECUPERACIÓN DE CONTRASEÑA
    @Transactional
    public void procesarRecuperacionPassword(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        // Regla de Oro de Ciberseguridad: Si el correo no existe, no le avisamos al Frontend
        // para evitar que atacantes adivinen qué correos están registrados en nuestra app.
        if (usuarioOpt.isEmpty()) {
            System.out.println("Solicitud de recuperación ignorada (Correo no registrado): " + email);
            return; 
        }

        Usuario usuario = usuarioOpt.get();

        // 1. Generamos una clave temporal de 8 caracteres al azar usando un fragmento de un UUID
        String nuevaClaveTemporal = UUID.randomUUID().toString().substring(0, 8);

        // 2. Hasheamos la nueva contraseña provisoria y la guardamos en la base de datos
        String passwordHash = passwordEncoder.encode(nuevaClaveTemporal);
        usuario.setPasswordHash(passwordHash);
        usuarioRepository.save(usuario);

        // 3. Enviamos el correo electrónico con el texto redactado
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tu_correo_real@gmail.com"); // Debe ser el mismo correo de tu .properties
            message.setTo(email);
            message.setSubject("Recuperación de Contraseña - Sanos y Salvos");
            message.setText("Hola " + usuario.getNombre() + ",\n\n" +
                    "Hemos recibido una solicitud para restablecer tu acceso a la plataforma Sanos y Salvos.\n\n" +
                    "Tu contraseña temporal de ingreso es: " + nuevaClaveTemporal + "\n\n" +
                    "Por tu propia seguridad, te aconsejamos modificarla desde tu perfil una vez que inicies sesión.\n\n" +
                    "Atentamente,\nEl Equipo de Sanos y Salvos.");

            mailSender.send(message);
            System.out.println("Correo enviado exitosamente a: " + email);
            
        } catch (Exception e) {
            // Si la conexión con Gmail falla (por ejemplo, falta de internet en el server), registramos el error
            System.err.println("Error crítico al intentar despachar el email a: " + email);
            e.printStackTrace();
            throw new RuntimeException("No se pudo despachar el email de recuperación.", e);
        }
    }

    @Transactional
    public void eliminarUsuario(String uuid) {
        usuarioRepository.deleteByUuid(uuid);
    }
}