package com.app.controlgastos.controller;

import com.app.controlgastos.dto.response.PatrimonioResponseDTO;
import com.app.controlgastos.service.PatrimonioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patrimonio")
@CrossOrigin(origins = "http://localhost:4200")
public class PatrimonioController {

    private final PatrimonioService patrimonioService;

    public PatrimonioController(PatrimonioService patrimonioService) {
        this.patrimonioService = patrimonioService;
    }

    @GetMapping("/actual")
    public PatrimonioResponseDTO obtenerActual() {
        return patrimonioService.obtenerActual();
    }

    @PutMapping("/actual")
    public PatrimonioResponseDTO actualizarIngresoActual(@jakarta.validation.Valid @RequestBody com.app.controlgastos.dto.request.PatrimonioRequestDTO request) {
        return patrimonioService.actualizarIngresoActual(request);
    }
}
