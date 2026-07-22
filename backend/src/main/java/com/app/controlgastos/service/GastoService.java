package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.GastoRequestDTO;
import com.app.controlgastos.dto.response.GastoResponseDTO;
import com.app.controlgastos.dto.response.ReporteGastoDTO;

import java.util.List;

public interface GastoService {
    GastoResponseDTO registrarGasto(GastoRequestDTO dto);
    GastoResponseDTO actualizarGasto(Long id, GastoRequestDTO dto);
    List<GastoResponseDTO> obtenerDelMesActual();
    List<GastoResponseDTO> obtenerProximosGastosTarjeta();
    List<ReporteGastoDTO> obtenerReporte(String periodo);
    void eliminarGasto(Long id);
    void pagarGasto(Long id);
    void pagarResumenTarjeta(String periodoStr);
    List<GastoResponseDTO> obtenerHistorialTarjetasPagadas();
}
