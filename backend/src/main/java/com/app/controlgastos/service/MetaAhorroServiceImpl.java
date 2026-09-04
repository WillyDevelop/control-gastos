package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.MetaAhorroRequestDTO;
import com.app.controlgastos.dto.response.MetaAhorroResponseDTO;
import com.app.controlgastos.model.MetaAhorro;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.MetaAhorroRepository;
import com.app.controlgastos.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetaAhorroServiceImpl implements MetaAhorroService {

    private final MetaAhorroRepository metaAhorroRepository;

    public MetaAhorroServiceImpl(MetaAhorroRepository metaAhorroRepository) {
        this.metaAhorroRepository = metaAhorroRepository;
    }

    @Override
    public List<MetaAhorroResponseDTO> obtenerTodas() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        return metaAhorroRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MetaAhorroResponseDTO crearMeta(MetaAhorroRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        
        // Desactivar las demás si esta es la primera o si queremos que la nueva sea activa? 
        // El requerimiento no especifica, la dejaremos inactiva por defecto y el usuario la activará.
        MetaAhorro meta = new MetaAhorro();
        meta.setNombre(dto.getNombre());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setUsuario(usuario);
        // montoActual y activaParaRedondeo ya tienen valores por defecto en la entidad

        return mapToDTO(metaAhorroRepository.save(meta));
    }

    @Override
    @Transactional
    public MetaAhorroResponseDTO activarMeta(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        
        MetaAhorro metaAActivar = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!metaAActivar.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        // Desactivar la actualmente activa
        metaAhorroRepository.findByUsuarioIdAndActivaParaRedondeoTrue(usuario.getId())
                .ifPresent(metaActiva -> {
                    metaActiva.setActivaParaRedondeo(false);
                    metaAhorroRepository.save(metaActiva);
                });

        metaAActivar.setActivaParaRedondeo(true);
        return mapToDTO(metaAhorroRepository.save(metaAActivar));
    }

    @Override
    @Transactional
    public MetaAhorroResponseDTO desactivarMeta(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        
        MetaAhorro metaADesactivar = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!metaADesactivar.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        metaADesactivar.setActivaParaRedondeo(false);
        return mapToDTO(metaAhorroRepository.save(metaADesactivar));
    }

    @Override
    @Transactional
    public MetaAhorroResponseDTO actualizarMeta(Long id, MetaAhorroRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        
        MetaAhorro meta = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        meta.setNombre(dto.getNombre());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        if (dto.getMontoActual() != null) {
            meta.setMontoActual(dto.getMontoActual());
        }

        return mapToDTO(metaAhorroRepository.save(meta));
    }

    @Override
    @Transactional
    public void eliminarMeta(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        
        MetaAhorro meta = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        metaAhorroRepository.delete(meta);
    }

    private MetaAhorroResponseDTO mapToDTO(MetaAhorro meta) {
        MetaAhorroResponseDTO dto = new MetaAhorroResponseDTO();
        dto.setId(meta.getId());
        dto.setNombre(meta.getNombre());
        dto.setMontoObjetivo(meta.getMontoObjetivo());
        dto.setMontoActual(meta.getMontoActual());
        dto.setActivaParaRedondeo(meta.isActivaParaRedondeo());
        return dto;
    }
}
