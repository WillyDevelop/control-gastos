package com.app.controlgastos.controller;

import com.app.controlgastos.dto.request.TarjetaCreditoRequestDTO;
import com.app.controlgastos.dto.response.TarjetaCreditoResponseDTO;
import com.app.controlgastos.service.TarjetaCreditoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tarjetas")
public class TarjetaCreditoController {

    private final TarjetaCreditoService service;

    public TarjetaCreditoController(TarjetaCreditoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarjetaCreditoResponseDTO crearTarjeta(@RequestBody TarjetaCreditoRequestDTO dto) {
        return service.crearTarjeta(dto);
    }

    @GetMapping
    public List<TarjetaCreditoResponseDTO> obtenerTarjetas() {
        return service.obtenerTarjetas();
    }

    @GetMapping("/{id}")
    public TarjetaCreditoResponseDTO obtenerTarjeta(@PathVariable Long id) {
        return service.obtenerTarjetaPorId(id);
    }

    @PutMapping("/{id}")
    public TarjetaCreditoResponseDTO actualizarTarjeta(@PathVariable Long id, @RequestBody TarjetaCreditoRequestDTO dto) {
        return service.actualizarTarjeta(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTarjeta(@PathVariable Long id) {
        service.eliminarTarjeta(id);
    }
}
