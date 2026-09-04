package com.app.controlgastos.controller;

import com.app.controlgastos.dto.response.AdminUsuarioDTO;
import com.app.controlgastos.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<AdminUsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PatchMapping("/usuarios/{id}/toggle-activo")
    public ResponseEntity<AdminUsuarioDTO> toggleEstadoUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleEstadoUsuario(id));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        adminService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
