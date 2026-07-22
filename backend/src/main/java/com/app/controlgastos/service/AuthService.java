package com.app.controlgastos.service;

import com.app.controlgastos.dto.*;
import com.app.controlgastos.model.*;
import com.app.controlgastos.repository.*;
import com.app.controlgastos.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TokenVerificacionRepository tokenVerificacionRepository;
    @Autowired private TokenRecuperacionRepository tokenRecuperacionRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private EmailService emailService;

    @Transactional
    public void registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setActivo(true); // Activado directamente
        usuario.setRol(Rol.ROLE_USER);
        usuarioRepository.save(usuario);

        /*
        String token = UUID.randomUUID().toString();
        TokenVerificacion tv = new TokenVerificacion(token, usuario, LocalDateTime.now().plusHours(24));
        tokenVerificacionRepository.save(tv);

        String link = "http://localhost:4200/verificar-cuenta?token=" + token;
        
        String htmlBody = String.format(
            "<div style=\"font-family: 'Inter', sans-serif; background-color: #F8FAFC; padding: 40px; text-align: center; border: 3px solid #0F172A; border-radius: 16px; max-width: 500px; margin: 0 auto; box-shadow: 6px 6px 0px 0px #0F172A;\">" +
            "  <h2 style=\"color: #0F172A; font-size: 24px; font-weight: 800; text-transform: uppercase; margin-bottom: 20px;\">Confirma tu Cuenta</h2>" +
            "  <p style=\"color: #64748B; font-size: 16px; font-weight: 500; margin-bottom: 30px;\">¡Gracias por registrarte! Haz clic en el botón de abajo para activar tu cuenta y empezar a controlar tus gastos.</p>" +
            "  <a href=\"%s\" style=\"display: inline-block; background-color: #6366F1; color: #FFFFFF; font-weight: 700; text-decoration: none; padding: 14px 28px; border: 3px solid #0F172A; border-radius: 12px; box-shadow: 4px 4px 0px 0px #0F172A; font-size: 16px;\">Activar Cuenta</a>" +
            "  <p style=\"color: #94A3B8; font-size: 12px; margin-top: 30px;\">Este enlace expira en 24 horas.</p>" +
            "</div>", link
        );

        emailService.sendEmail(usuario.getEmail(), "Confirma tu cuenta - CtrlGastos", htmlBody);
        */
    }

    @Transactional
    public void verificar(String token) {
        TokenVerificacion tv = tokenVerificacionRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        
        if (tv.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token ha expirado");
        }

        Usuario usuario = tv.getUsuario();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        tokenVerificacionRepository.delete(tv);
    }

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("Cuenta no activada. Revisa tu email.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String jwt = jwtService.generateToken(usuario);
        return new AuthResponse(jwt, usuario.getEmail(), usuario.getNombre());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(usuario -> {
            // Limpiar tokens anteriores si existen para este usuario
            tokenRecuperacionRepository.deleteByUsuario(usuario);

            String token = UUID.randomUUID().toString();
            TokenRecuperacion tr = new TokenRecuperacion(token, usuario, LocalDateTime.now().plusMinutes(15));
            tokenRecuperacionRepository.save(tr);

            String link = "http://localhost:4200/restablecer-password?token=" + token;

            String htmlBody = String.format(
                "<div style=\"font-family: 'Inter', sans-serif; background-color: #F8FAFC; padding: 40px; text-align: center; border: 3px solid #0F172A; border-radius: 16px; max-width: 500px; margin: 0 auto; box-shadow: 6px 6px 0px 0px #0F172A;\">" +
                "  <h2 style=\"color: #0F172A; font-size: 24px; font-weight: 800; text-transform: uppercase; margin-bottom: 20px;\">Recuperación de Contraseña</h2>" +
                "  <p style=\"color: #64748B; font-size: 16px; font-weight: 500; margin-bottom: 30px;\">Recibimos una solicitud para restablecer tu contraseña. Haz clic en el botón de abajo para cambiarla.</p>" +
                "  <a href=\"%s\" style=\"display: inline-block; background-color: #6366F1; color: #FFFFFF; font-weight: 700; text-decoration: none; padding: 14px 28px; border: 3px solid #0F172A; border-radius: 12px; box-shadow: 4px 4px 0px 0px #0F172A; font-size: 16px;\">Restablecer Contraseña</a>" +
                "  <p style=\"color: #94A3B8; font-size: 12px; margin-top: 30px;\">Este enlace expira en 15 minutos por razones de seguridad estricta.</p>" +
                "</div>", link
            );

            emailService.sendEmail(usuario.getEmail(), "Recuperar Contraseña - CtrlGastos", htmlBody);
        });
    }

    @Transactional
    public void resetPassword(RestablecerPasswordRequest request) {
        if (request.getNuevaPassword() == null || request.getConfirmarPassword() == null) {
            throw new IllegalArgumentException("Las contraseñas no pueden ser nulas");
        }
        if (!request.getNuevaPassword().equals(request.getConfirmarPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        TokenRecuperacion tr = tokenRecuperacionRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        
        if (tr.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token ha expirado");
        }

        Usuario usuario = tr.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);
        tokenRecuperacionRepository.delete(tr);
    }
}
