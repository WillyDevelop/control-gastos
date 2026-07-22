package com.app.controlgastos.controller;

import com.app.controlgastos.dto.response.DeudaResponseDTO;
import com.app.controlgastos.service.DeudaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deudas")
@CrossOrigin(origins = "http://localhost:4200")
public class DeudaController {

    private final DeudaService deudaService;

    public DeudaController(DeudaService deudaService) {
        this.deudaService = deudaService;
    }

    @GetMapping("/por-cobrar")
    public List<DeudaResponseDTO> obtenerPorCobrar() {
        return deudaService.obtenerPorCobrar();
    }

    @PutMapping("/{id}/liquidar")
    public DeudaResponseDTO liquidar(@PathVariable Long id) {
        return deudaService.liquidar(id);
    }
}
