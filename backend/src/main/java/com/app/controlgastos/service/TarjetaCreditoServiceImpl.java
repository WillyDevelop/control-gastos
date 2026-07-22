package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.TarjetaCreditoRequestDTO;
import com.app.controlgastos.dto.response.TarjetaCreditoResponseDTO;
import com.app.controlgastos.model.TarjetaCredito;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.TarjetaCreditoRepository;
import com.app.controlgastos.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarjetaCreditoServiceImpl implements TarjetaCreditoService {

    private final TarjetaCreditoRepository repository;

    public TarjetaCreditoServiceImpl(TarjetaCreditoRepository repository) {
        this.repository = repository;
    }

    @Override
    public TarjetaCreditoResponseDTO crearTarjeta(TarjetaCreditoRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        TarjetaCredito tarjeta = new TarjetaCredito();
        tarjeta.setNombre(dto.getNombre());
        tarjeta.setDiaCierre(dto.getDiaCierre());
        tarjeta.setDiaVencimiento(dto.getDiaVencimiento());
        tarjeta.setUsuario(usuario);
        return mapToDTO(repository.save(tarjeta));
    }

    @Override
    public List<TarjetaCreditoResponseDTO> obtenerTarjetas() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        return repository.findByUsuarioId(usuario.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TarjetaCreditoResponseDTO obtenerTarjetaPorId(Long id) {
        return mapToDTO(getTarjetaEntity(id));
    }

    @Override
    public void eliminarTarjeta(Long id) {
        TarjetaCredito tarjeta = getTarjetaEntity(id);
        repository.delete(tarjeta);
    }

    @Override
    public TarjetaCreditoResponseDTO actualizarTarjeta(Long id, TarjetaCreditoRequestDTO dto) {
        TarjetaCredito tarjeta = getTarjetaEntity(id);
        tarjeta.setNombre(dto.getNombre());
        tarjeta.setDiaCierre(dto.getDiaCierre());
        tarjeta.setDiaVencimiento(dto.getDiaVencimiento());
        return mapToDTO(repository.save(tarjeta));
    }

    private TarjetaCredito getTarjetaEntity(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        TarjetaCredito tarjeta = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta no encontrada"));
        if (!tarjeta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a esta tarjeta");
        }
        return tarjeta;
    }

    private TarjetaCreditoResponseDTO mapToDTO(TarjetaCredito tarjeta) {
        TarjetaCreditoResponseDTO dto = new TarjetaCreditoResponseDTO();
        dto.setId(tarjeta.getId());
        dto.setNombre(tarjeta.getNombre());
        dto.setDiaCierre(tarjeta.getDiaCierre());
        dto.setDiaVencimiento(tarjeta.getDiaVencimiento());
        return dto;
    }
}
