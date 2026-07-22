package com.app.controlgastos.controller;

import com.app.controlgastos.dto.request.PlantillaGastoRequestDTO;
import com.app.controlgastos.dto.response.PlantillaGastoResponseDTO;
import com.app.controlgastos.service.PlantillaGastoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plantillas")
@CrossOrigin(origins = "http://localhost:4200")
public class PlantillaGastoController {

    private final PlantillaGastoService plantillaService;

    public PlantillaGastoController(PlantillaGastoService plantillaService) {
        this.plantillaService = plantillaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlantillaGastoResponseDTO crearPlantilla(@Valid @RequestBody PlantillaGastoRequestDTO request) {
        return plantillaService.crearPlantilla(request);
    }

    @GetMapping
    public List<PlantillaGastoResponseDTO> obtenerPlantillas() {
        return plantillaService.obtenerPlantillas();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPlantilla(@PathVariable Long id) {
        plantillaService.eliminarPlantilla(id);
    }
}
