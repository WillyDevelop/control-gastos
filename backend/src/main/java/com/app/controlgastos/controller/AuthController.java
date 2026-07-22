package com.app.controlgastos.controller;

import com.app.controlgastos.dto.*;
import com.app.controlgastos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequest request) {
        try {
            authService.registrar(request);
            return ResponseEntity.ok().body("{\"message\":\"¡Cuenta creada con éxito!\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Error al registrar usuario: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmar(@RequestParam String token) {
        try {
            authService.verificar(token);
            return ResponseEntity.ok().body("{\"message\":\"Cuenta activada correctamente\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/olvido-password")
    public ResponseEntity<?> olvidoPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().body("{\"message\":\"Si el email existe, se envió un enlace de recuperación\"}");
    }

    @PostMapping("/restablecer")
    public ResponseEntity<?> restablecer(@RequestBody RestablecerPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok().body("{\"message\":\"Contraseña restablecida correctamente\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
