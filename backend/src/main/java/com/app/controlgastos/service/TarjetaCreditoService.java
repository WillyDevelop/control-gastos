package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.TarjetaCreditoRequestDTO;
import com.app.controlgastos.dto.response.TarjetaCreditoResponseDTO;

import java.util.List;

public interface TarjetaCreditoService {
    TarjetaCreditoResponseDTO crearTarjeta(TarjetaCreditoRequestDTO dto);
    List<TarjetaCreditoResponseDTO> obtenerTarjetas();
    TarjetaCreditoResponseDTO obtenerTarjetaPorId(Long id);
    void eliminarTarjeta(Long id);
    TarjetaCreditoResponseDTO actualizarTarjeta(Long id, TarjetaCreditoRequestDTO dto);
}
