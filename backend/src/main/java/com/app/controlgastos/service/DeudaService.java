package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.DeudaResponseDTO;
import java.util.List;

public interface DeudaService {
    List<DeudaResponseDTO> obtenerPorCobrar();
    DeudaResponseDTO liquidar(Long id);
}
