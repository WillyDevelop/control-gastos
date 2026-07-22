package com.app.controlgastos.controller;

import com.app.controlgastos.dto.request.GastoRequestDTO;
import com.app.controlgastos.dto.response.GastoResponseDTO;
import com.app.controlgastos.dto.response.ReporteGastoDTO;
import com.app.controlgastos.service.GastoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gastos")
@CrossOrigin(origins = "http://localhost:4200")
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GastoResponseDTO registrarGasto(@Valid @RequestBody GastoRequestDTO request) {
        return gastoService.registrarGasto(request);
    }

    @PutMapping("/{id}")
    public GastoResponseDTO actualizarGasto(@PathVariable Long id, @Valid @RequestBody GastoRequestDTO request) {
        return gastoService.actualizarGasto(id, request);
    }

    @GetMapping
    public List<GastoResponseDTO> obtenerGastos() {
        return gastoService.obtenerDelMesActual();
    }

    @GetMapping("/tarjeta/proximos")
    public List<GastoResponseDTO> obtenerProximosGastosTarjeta() {
        return gastoService.obtenerProximosGastosTarjeta();
    }

    @GetMapping("/tarjeta/historial")
    public List<GastoResponseDTO> obtenerHistorialTarjetasPagadas() {
        return gastoService.obtenerHistorialTarjetasPagadas();
    }

    @PutMapping("/{id}/pagar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagarGasto(@PathVariable Long id) {
        gastoService.pagarGasto(id);
    }

    @PutMapping("/tarjeta/pagar-resumen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagarResumenTarjeta(@RequestParam String periodo) {
        gastoService.pagarResumenTarjeta(periodo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarGasto(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
    }

    @GetMapping("/reporte")
    public List<ReporteGastoDTO> reporteGastos(@RequestParam(defaultValue = "mes") String periodo) {
        return gastoService.obtenerReporte(periodo);
    }

    @GetMapping("/calendario")
    public List<ReporteGastoDTO> calendarioGastos(@RequestParam int mes, @RequestParam int anio) {
        return gastoService.obtenerReporte("mes");
    }
}
