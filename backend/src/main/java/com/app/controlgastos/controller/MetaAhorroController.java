package com.app.controlgastos.controller;

import com.app.controlgastos.dto.request.MetaAhorroRequestDTO;
import com.app.controlgastos.dto.response.MetaAhorroResponseDTO;
import com.app.controlgastos.service.MetaAhorroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metas")
@CrossOrigin(origins = "http://localhost:4200")
public class MetaAhorroController {

    private final MetaAhorroService metaAhorroService;

    public MetaAhorroController(MetaAhorroService metaAhorroService) {
        this.metaAhorroService = metaAhorroService;
    }

    @GetMapping
    public List<MetaAhorroResponseDTO> obtenerTodas() {
        return metaAhorroService.obtenerTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetaAhorroResponseDTO crearMeta(@jakarta.validation.Valid @RequestBody MetaAhorroRequestDTO request) {
        return metaAhorroService.crearMeta(request);
    }

    @PutMapping("/{id}/activar")
    public MetaAhorroResponseDTO activarMeta(@PathVariable Long id) {
        return metaAhorroService.activarMeta(id);
    }
}
