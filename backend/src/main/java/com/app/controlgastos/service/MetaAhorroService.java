package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.MetaAhorroRequestDTO;
import com.app.controlgastos.dto.response.MetaAhorroResponseDTO;
import java.util.List;

public interface MetaAhorroService {
    List<MetaAhorroResponseDTO> obtenerTodas();
    MetaAhorroResponseDTO crearMeta(MetaAhorroRequestDTO dto);
    MetaAhorroResponseDTO activarMeta(Long id);
}
