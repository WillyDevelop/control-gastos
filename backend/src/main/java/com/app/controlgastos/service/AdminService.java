package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.AdminUsuarioDTO;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.*;
import com.app.controlgastos.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GastoRepository gastoRepository;
    @Autowired private TarjetaCreditoRepository tarjetaCreditoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private TokenVerificacionRepository tokenVerificacionRepository;
    @Autowired private TokenRecuperacionRepository tokenRecuperacionRepository;

    @Transactional(readOnly = true)
    public List<AdminUsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new AdminUsuarioDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getNombre(),
                        u.isActivo(),
                        u.getRol(),
                        gastoRepository.countByUsuarioId(u.getId()),
                        tarjetaCreditoRepository.countByUsuarioId(u.getId())
                ))
                .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminUsuarioDTO toggleEstadoUsuario(Long id) {
        String emailActual = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEmail().equalsIgnoreCase(emailActual)) {
            throw new IllegalArgumentException("No puedes desactivar tu propia cuenta de administrador.");
        }

        usuario.setActivo(!usuario.isActivo());
        Usuario guardado = usuarioRepository.save(usuario);

        return new AdminUsuarioDTO(
                guardado.getId(),
                guardado.getEmail(),
                guardado.getNombre(),
                guardado.isActivo(),
                guardado.getRol(),
                gastoRepository.countByUsuarioId(guardado.getId()),
                tarjetaCreditoRepository.countByUsuarioId(guardado.getId())
        );
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        String emailActual = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (usuario.getEmail().equalsIgnoreCase(emailActual)) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta de administrador.");
        }

        tokenVerificacionRepository.deleteByUsuario(usuario);
        tokenRecuperacionRepository.deleteByUsuario(usuario);
        gastoRepository.deleteByUsuario(usuario);
        tarjetaCreditoRepository.deleteByUsuario(usuario);
        categoriaRepository.deleteByUsuario(usuario);

        usuarioRepository.delete(usuario);
    }
}
