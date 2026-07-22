package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.PatrimonioResponseDTO;
import com.app.controlgastos.model.PatrimonioMensual;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.PatrimonioMensualRepository;
import com.app.controlgastos.repository.GastoRepository;
import com.app.controlgastos.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PatrimonioServiceImpl implements PatrimonioService {

    private final PatrimonioMensualRepository patrimonioRepository;
    private final GastoRepository gastoRepository;

    public PatrimonioServiceImpl(PatrimonioMensualRepository patrimonioRepository, GastoRepository gastoRepository) {
        this.patrimonioRepository = patrimonioRepository;
        this.gastoRepository = gastoRepository;
    }

    @Override
    public PatrimonioResponseDTO obtenerActual() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();
        int anio = today.getYear();
        int mes = today.getMonthValue();

        PatrimonioMensual pm = patrimonioRepository.findByMesAndAnioAndUsuarioId(mes, anio, usuario.getId())
                .orElseGet(() -> crearPatrimonioPorDefecto(mes, anio, usuario));

        BigDecimal totalPagado = gastoRepository.sumPagadoByMesAndAnioAndUsuarioId(anio, mes, usuario.getId());
        if (totalPagado == null) totalPagado = BigDecimal.ZERO;

        BigDecimal totalIngresos = gastoRepository.sumIngresosByMesAndAnioAndUsuarioId(anio, mes, usuario.getId());
        if (totalIngresos == null) totalIngresos = BigDecimal.ZERO;

        pm.setIngresoTotal(totalIngresos);
        pm.setSaldoActual(totalIngresos.subtract(totalPagado));
        patrimonioRepository.save(pm);

        LocalDate nextMonth = today.plusMonths(1);
        BigDecimal gastoMesQueViene = gastoRepository.sumTarjetasPendientesPorMes(usuario.getId(), nextMonth.getYear(), nextMonth.getMonthValue());
        if (gastoMesQueViene == null) gastoMesQueViene = BigDecimal.ZERO;

        PatrimonioResponseDTO dto = new PatrimonioResponseDTO();
        dto.setId(pm.getId());
        dto.setMes(pm.getMes());
        dto.setAnio(pm.getAnio());
        dto.setIngresoTotal(pm.getIngresoTotal());
        dto.setSaldoActual(pm.getSaldoActual());
        dto.setTotalPagado(totalPagado);
        dto.setGastoMesQueViene(gastoMesQueViene);
        dto.setFechaIngreso(pm.getFechaIngreso());
        return dto;
    }

    @Override
    public PatrimonioResponseDTO actualizarIngresoActual(com.app.controlgastos.dto.request.PatrimonioRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();
        int anio = today.getYear();
        int mes = today.getMonthValue();

        PatrimonioMensual pm = patrimonioRepository.findByMesAndAnioAndUsuarioId(mes, anio, usuario.getId())
                .orElseGet(() -> crearPatrimonioPorDefecto(mes, anio, usuario));

        if (dto.getFechaIngreso() != null) {
            pm.setFechaIngreso(dto.getFechaIngreso());
            patrimonioRepository.save(pm);
        }

        return obtenerActual();
    }

    private PatrimonioMensual crearPatrimonioPorDefecto(int mes, int anio, Usuario usuario) {
        PatrimonioMensual pm = new PatrimonioMensual();
        pm.setMes(mes);
        pm.setAnio(anio);
        pm.setIngresoTotal(BigDecimal.ZERO);
        pm.setSaldoActual(BigDecimal.ZERO);
        pm.setUsuario(usuario);
        return patrimonioRepository.save(pm);
    }
}
