package com.app.controlgastos.controller;

import com.app.controlgastos.dto.request.PerfilRequestDTO;
import com.app.controlgastos.dto.request.SeguridadRequestDTO;
import com.app.controlgastos.dto.response.UsuarioResponseDTO;
import com.app.controlgastos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(@Valid @RequestBody PerfilRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(dto));
    }

    @PutMapping("/seguridad")
    public ResponseEntity<UsuarioResponseDTO> actualizarSeguridad(@Valid @RequestBody SeguridadRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarSeguridad(dto));
    }
}
