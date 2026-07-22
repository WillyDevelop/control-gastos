package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.PatrimonioResponseDTO;

public interface PatrimonioService {
    PatrimonioResponseDTO obtenerActual();
    PatrimonioResponseDTO actualizarIngresoActual(com.app.controlgastos.dto.request.PatrimonioRequestDTO request);
}
