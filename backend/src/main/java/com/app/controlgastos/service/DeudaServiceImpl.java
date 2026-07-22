package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.DeudaResponseDTO;
import com.app.controlgastos.model.Deuda;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.DeudaRepository;
import com.app.controlgastos.repository.PatrimonioMensualRepository;
import com.app.controlgastos.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeudaServiceImpl implements DeudaService {

    private final DeudaRepository deudaRepository;
    private final PatrimonioMensualRepository patrimonioMensualRepository;

    public DeudaServiceImpl(DeudaRepository deudaRepository, PatrimonioMensualRepository patrimonioMensualRepository) {
        this.deudaRepository = deudaRepository;
        this.patrimonioMensualRepository = patrimonioMensualRepository;
    }

    @Override
    public List<DeudaResponseDTO> obtenerPorCobrar() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        return deudaRepository.findByAcreedorIdAndLiquidadaFalse(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeudaResponseDTO liquidar(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Deuda deuda = deudaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deuda no encontrada"));

        if (!deuda.getAcreedor().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para liquidar esta deuda");
        }

        if (!deuda.isLiquidada()) {
            deuda.setLiquidada(true);
            deudaRepository.save(deuda);

            // Reintegrar al patrimonio mensual actual
            LocalDate today = LocalDate.now();
            patrimonioMensualRepository.findByMesAndAnioAndUsuarioId(today.getMonthValue(), today.getYear(), usuario.getId())
                    .ifPresent(pm -> {
                        pm.setSaldoActual(pm.getSaldoActual().add(deuda.getMontoDeuda()));
                        patrimonioMensualRepository.save(pm);
                    });
        }

        return mapToDTO(deuda);
    }

    private DeudaResponseDTO mapToDTO(Deuda deuda) {
        DeudaResponseDTO dto = new DeudaResponseDTO();
        dto.setId(deuda.getId());
        dto.setGastoDescripcion(deuda.getGasto().getDescripcion());
        dto.setDeudorEmail(deuda.getDeudor().getEmail());
        // En una app real se mostraría el nombre del deudor si está disponible, aquí usaremos el email como backup
        dto.setDeudorNombre(deuda.getDeudor().getEmail());
        dto.setMontoDeuda(deuda.getMontoDeuda());
        dto.setLiquidada(deuda.isLiquidada());
        return dto;
    }
}
