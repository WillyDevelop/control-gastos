package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.CategoriaResponseDTO;
import com.app.controlgastos.dto.response.PresupuestoCategoriaDTO;
import com.app.controlgastos.dto.request.CategoriaRequestDTO;
import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDTO> obtenerTodas();
    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto);
    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto);
    void eliminarCategoria(Long id);
    List<PresupuestoCategoriaDTO> obtenerPresupuestosDelMes();
}
