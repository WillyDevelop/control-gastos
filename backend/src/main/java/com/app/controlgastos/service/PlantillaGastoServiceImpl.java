package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.PlantillaGastoRequestDTO;
import com.app.controlgastos.dto.response.CategoriaResponseDTO;
import com.app.controlgastos.dto.response.PlantillaGastoResponseDTO;
import com.app.controlgastos.model.Categoria;
import com.app.controlgastos.model.PlantillaGasto;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.CategoriaRepository;
import com.app.controlgastos.repository.PlantillaGastoRepository;
import com.app.controlgastos.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantillaGastoServiceImpl implements PlantillaGastoService {

    private final PlantillaGastoRepository plantillaRepository;
    private final CategoriaRepository categoriaRepository;

    public PlantillaGastoServiceImpl(PlantillaGastoRepository plantillaRepository, CategoriaRepository categoriaRepository) {
        this.plantillaRepository = plantillaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public PlantillaGastoResponseDTO crearPlantilla(PlantillaGastoRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + dto.getCategoriaId()));

        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a esta categoría");
        }

        PlantillaGasto plantilla = new PlantillaGasto();
        plantilla.setNombrePlantilla(dto.getNombrePlantilla());
        plantilla.setDescripcion(dto.getDescripcion());
        plantilla.setMonto(dto.getMonto());
        plantilla.setCategoria(categoria);
        plantilla.setMetodoPago(dto.getMetodoPago());
        plantilla.setUsuario(usuario);

        plantilla = plantillaRepository.save(plantilla);
        return mapToDTO(plantilla);
    }

    @Override
    public List<PlantillaGastoResponseDTO> obtenerPlantillas() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        return plantillaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarPlantilla(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        PlantillaGasto plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plantilla no encontrada con id: " + id));
        
        if (!plantilla.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a esta plantilla");
        }
        
        plantillaRepository.delete(plantilla);
    }

    private PlantillaGastoResponseDTO mapToDTO(PlantillaGasto plantilla) {
        PlantillaGastoResponseDTO dto = new PlantillaGastoResponseDTO();
        dto.setId(plantilla.getId());
        dto.setNombrePlantilla(plantilla.getNombrePlantilla());
        dto.setDescripcion(plantilla.getDescripcion());
        dto.setMonto(plantilla.getMonto());
        dto.setMetodoPago(plantilla.getMetodoPago());
        
        CategoriaResponseDTO catDto = new CategoriaResponseDTO();
        catDto.setId(plantilla.getCategoria().getId());
        catDto.setNombre(plantilla.getCategoria().getNombre());
        catDto.setIcono(plantilla.getCategoria().getIcono());
        catDto.setTipo(plantilla.getCategoria().getTipo());
        catDto.setLimiteMensual(plantilla.getCategoria().getLimiteMensual());
        dto.setCategoria(catDto);
        
        return dto;
    }
}
