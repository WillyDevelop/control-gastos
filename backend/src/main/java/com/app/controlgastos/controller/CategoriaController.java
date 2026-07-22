package com.app.controlgastos.controller;

import com.app.controlgastos.dto.response.CategoriaResponseDTO;
import com.app.controlgastos.dto.response.PresupuestoCategoriaDTO;
import com.app.controlgastos.service.CategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaService.obtenerTodas();
    }

    @GetMapping("/presupuestos")
    public List<PresupuestoCategoriaDTO> obtenerPresupuestos() {
        return categoriaService.obtenerPresupuestosDelMes();
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public CategoriaResponseDTO crearCategoria(@jakarta.validation.Valid @RequestBody com.app.controlgastos.dto.request.CategoriaRequestDTO request) {
        return categoriaService.crearCategoria(request);
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO actualizarCategoria(@PathVariable Long id, @jakarta.validation.Valid @RequestBody com.app.controlgastos.dto.request.CategoriaRequestDTO request) {
        return categoriaService.actualizarCategoria(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
    }
}
