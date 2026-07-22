package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.PerfilRequestDTO;
import com.app.controlgastos.dto.request.SeguridadRequestDTO;
import com.app.controlgastos.dto.response.UsuarioResponseDTO;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    private Usuario getUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    @Transactional
    public UsuarioResponseDTO actualizarPerfil(PerfilRequestDTO dto) {
        Usuario usuario = getUsuarioActual();
        usuario.setNombre(dto.getNombre());
        Usuario saved = usuarioRepository.save(usuario);
        return mapToDTO(saved);
    }

    @Transactional
    public UsuarioResponseDTO actualizarSeguridad(SeguridadRequestDTO dto) {
        Usuario usuario = getUsuarioActual();

        // Verificar contraseña actual
        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getPassword())) {
            throw new RuntimeException("401:Contraseña actual incorrecta");
        }

        boolean changes = false;

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            changes = true;
        }

        if (dto.getNewEmail() != null && !dto.getNewEmail().isBlank()
                && !dto.getNewEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getNewEmail())) {
                throw new RuntimeException("400:El correo electrónico ya está en uso");
            }

            String token = UUID.randomUUID().toString();
            usuario.setNuevoEmailPendiente(dto.getNewEmail());
            usuario.setTokenCambioEmail(token);
            changes = true;

            emailService.sendEmail(
                    usuario.getEmail(),
                    "Confirmación de cambio de correo electrónico",
                    "Solicitaste cambiar tu email a: " + dto.getNewEmail()
                            + "\nToken de confirmación: " + token
            );
        }

        if (changes) {
            usuario = usuarioRepository.save(usuario);
        }

        return mapToDTO(usuario);
    }

    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        return dto;
    }
}
