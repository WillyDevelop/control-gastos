package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.PlantillaGastoRequestDTO;
import com.app.controlgastos.dto.response.PlantillaGastoResponseDTO;

import java.util.List;

public interface PlantillaGastoService {
    PlantillaGastoResponseDTO crearPlantilla(PlantillaGastoRequestDTO dto);
    List<PlantillaGastoResponseDTO> obtenerPlantillas();
    void eliminarPlantilla(Long id);
}
