package com.app.controlgastos.service;

import com.app.controlgastos.dto.request.GastoRequestDTO;
import com.app.controlgastos.dto.response.CategoriaResponseDTO;
import com.app.controlgastos.dto.response.GastoResponseDTO;
import com.app.controlgastos.dto.response.ReporteGastoDTO;
import com.app.controlgastos.model.Categoria;
import com.app.controlgastos.model.Gasto;
import com.app.controlgastos.model.MetodoPago;
import com.app.controlgastos.model.PatrimonioMensual;
import com.app.controlgastos.model.TipoCategoria;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.CategoriaRepository;
import com.app.controlgastos.repository.GastoRepository;
import com.app.controlgastos.repository.PatrimonioMensualRepository;
import com.app.controlgastos.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PatrimonioMensualRepository patrimonioRepository;
    private final com.app.controlgastos.repository.MetaAhorroRepository metaAhorroRepository;
    private final com.app.controlgastos.repository.UsuarioRepository usuarioRepository;
    private final com.app.controlgastos.repository.DeudaRepository deudaRepository;
    private final com.app.controlgastos.repository.TarjetaCreditoRepository tarjetaRepository;

    public GastoServiceImpl(
            GastoRepository gastoRepository, 
            CategoriaRepository categoriaRepository, 
            PatrimonioMensualRepository patrimonioRepository, 
            com.app.controlgastos.repository.MetaAhorroRepository metaAhorroRepository,
            com.app.controlgastos.repository.UsuarioRepository usuarioRepository,
            com.app.controlgastos.repository.DeudaRepository deudaRepository,
            com.app.controlgastos.repository.TarjetaCreditoRepository tarjetaRepository) {
        this.gastoRepository = gastoRepository;
        this.categoriaRepository = categoriaRepository;
        this.patrimonioRepository = patrimonioRepository;
        this.metaAhorroRepository = metaAhorroRepository;
        this.usuarioRepository = usuarioRepository;
        this.deudaRepository = deudaRepository;
        this.tarjetaRepository = tarjetaRepository;
    }

    @Override
    @Transactional
    public GastoResponseDTO registrarGasto(GastoRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + dto.getCategoriaId()));

        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a esta categoría");
        }

        Gasto gasto = new Gasto();
        gasto.setUsuario(usuario);
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(dto.getMonto());
        LocalDate fechaReal = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();
        gasto.setFecha(fechaReal);
        gasto.setCategoria(categoria);
        gasto.setMetodoPago(dto.getMetodoPago());
        gasto.setEsRecurrente(dto.isEsRecurrente());
        gasto.setNotas(dto.getNotas());
        gasto.setEntidadPago(dto.getEntidadPago());
        com.app.controlgastos.model.TarjetaCredito tarjeta = null;
        if (dto.getMetodoPago() == MetodoPago.TARJETA_CREDITO) {
            gasto.setPagado(false);
            if (dto.getTarjetaCreditoId() == null) {
                throw new IllegalArgumentException("Debe seleccionar una tarjeta de crédito");
            }
            tarjeta = tarjetaRepository.findById(dto.getTarjetaCreditoId())
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta no encontrada"));
            gasto.setTarjetaCredito(tarjeta);
        } else {
            gasto.setTarjetaCredito(null);
            gasto.setPagado(dto.isPagado());
        }

        LocalDate periodoFinancieroCalc = fechaReal.withDayOfMonth(1);
        if (dto.getMetodoPago() == MetodoPago.TARJETA_CREDITO && tarjeta != null) {
            if (fechaReal.getDayOfMonth() <= tarjeta.getDiaCierre()) {
                periodoFinancieroCalc = periodoFinancieroCalc.plusMonths(1);
            } else {
                periodoFinancieroCalc = periodoFinancieroCalc.plusMonths(2);
            }
        }
        final LocalDate periodoFinanciero = periodoFinancieroCalc;
        gasto.setPeriodoFinanciero(periodoFinanciero);

        gasto = gastoRepository.save(gasto);

        if (gasto.isPagado()) {
            actualizarPatrimonio(periodoFinanciero.getMonthValue(), periodoFinanciero.getYear(), dto.getMonto(), categoria.getTipo(), usuario);
            
            // Lógica de redondeo automático
            if (dto.getMetodoPago() != MetodoPago.TARJETA_CREDITO) {
                metaAhorroRepository.findByUsuarioIdAndActivaParaRedondeoTrue(usuario.getId())
                        .ifPresent(meta -> {
                            BigDecimal monto = dto.getMonto();
                            // Redondear al millar superior
                            BigDecimal mil = new BigDecimal("1000");
                            BigDecimal redondeado = monto.divide(mil, 0, java.math.RoundingMode.CEILING).multiply(mil);
                            BigDecimal diferencia = redondeado.subtract(monto);
                            
                            if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
                                // Sumar a la meta de ahorro
                                meta.setMontoActual(meta.getMontoActual().add(diferencia));
                                metaAhorroRepository.save(meta);
                                
                                // Restar del patrimonio (como si fuera un gasto extra)
                                actualizarPatrimonio(periodoFinanciero.getMonthValue(), periodoFinanciero.getYear(), diferencia, TipoCategoria.GASTO, usuario);
                            }
                        });
            }
        }

        // Lógica de Gastos Compartidos (Fase 4)
        if (dto.isDividirGasto() && dto.getEmailDeudor() != null && dto.getPorcentajeDeuda() != null) {
            Usuario deudor = usuarioRepository.findByEmail(dto.getEmailDeudor())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró al usuario deudor con email: " + dto.getEmailDeudor()));
            
            BigDecimal montoDeuda = dto.getMonto().multiply(dto.getPorcentajeDeuda()).divide(new BigDecimal("100"));
            
            com.app.controlgastos.model.Deuda deuda = new com.app.controlgastos.model.Deuda();
            deuda.setGasto(gasto);
            deuda.setAcreedor(usuario);
            deuda.setDeudor(deudor);
            deuda.setMontoDeuda(montoDeuda);
            deuda.setLiquidada(false);
            
            deudaRepository.save(deuda);
        }

        return mapToDTO(gasto);
    }

    @Override
    @Transactional
    public GastoResponseDTO actualizarGasto(Long id, GastoRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado con id: " + id));
        
        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a este gasto");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + dto.getCategoriaId()));

        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a esta categoría");
        }

        // Revertir efecto en patrimonio si ya estaba pagado, para luego aplicar el nuevo
        if (gasto.isPagado()) {
            actualizarPatrimonio(gasto.getPeriodoFinanciero().getMonthValue(), gasto.getPeriodoFinanciero().getYear(), gasto.getMonto().negate(), gasto.getCategoria().getTipo(), usuario);
        }

        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(dto.getMonto());
        LocalDate fechaReal = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();
        gasto.setFecha(fechaReal);
        gasto.setCategoria(categoria);
        gasto.setMetodoPago(dto.getMetodoPago());
        
        gasto.setPagado(dto.isPagado());
        
        gasto.setNotas(dto.getNotas());
        gasto.setEntidadPago(dto.getEntidadPago());

        com.app.controlgastos.model.TarjetaCredito tarjeta = null;
        if (dto.getMetodoPago() == MetodoPago.TARJETA_CREDITO) {
            if (dto.getTarjetaCreditoId() == null) {
                throw new IllegalArgumentException("Debe seleccionar una tarjeta de crédito");
            }
            tarjeta = tarjetaRepository.findById(dto.getTarjetaCreditoId())
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta no encontrada"));
            gasto.setTarjetaCredito(tarjeta);
        } else {
            gasto.setTarjetaCredito(null);
        }

        LocalDate periodoFinancieroCalc;
        if (gasto.isPagado() && gasto.getPeriodoFinanciero() != null) {
            periodoFinancieroCalc = gasto.getPeriodoFinanciero();
        } else {
            periodoFinancieroCalc = fechaReal.withDayOfMonth(1);
            if (dto.getMetodoPago() == MetodoPago.TARJETA_CREDITO && tarjeta != null) {
                if (fechaReal.getDayOfMonth() <= tarjeta.getDiaCierre()) {
                    periodoFinancieroCalc = periodoFinancieroCalc.plusMonths(1);
                } else {
                    periodoFinancieroCalc = periodoFinancieroCalc.plusMonths(2);
                }
            }
        }
        
        gasto.setPeriodoFinanciero(periodoFinancieroCalc);

        gasto = gastoRepository.save(gasto);

        if (gasto.isPagado()) {
            actualizarPatrimonio(periodoFinancieroCalc.getMonthValue(), periodoFinancieroCalc.getYear(), dto.getMonto(), categoria.getTipo(), usuario);
        }

        return mapToDTO(gasto);
    }

    private void actualizarPatrimonio(int mes, int anio, BigDecimal monto, TipoCategoria tipoCategoria, Usuario usuario) {
        PatrimonioMensual pm = patrimonioRepository.findByMesAndAnioAndUsuarioId(mes, anio, usuario.getId())
                .orElseGet(() -> {
                    PatrimonioMensual nuevo = new PatrimonioMensual();
                    nuevo.setMes(mes);
                    nuevo.setAnio(anio);
                    nuevo.setIngresoTotal(new BigDecimal("10000.00")); // Default
                    nuevo.setSaldoActual(new BigDecimal("10000.00"));
                    nuevo.setUsuario(usuario);
                    return patrimonioRepository.save(nuevo);
                });

        if (tipoCategoria == TipoCategoria.GASTO) {
            pm.setSaldoActual(pm.getSaldoActual().subtract(monto));
        } else {
            pm.setSaldoActual(pm.getSaldoActual().add(monto));
            pm.setIngresoTotal(pm.getIngresoTotal().add(monto));
        }
        
        patrimonioRepository.save(pm);
    }

    @Override
    public List<GastoResponseDTO> obtenerDelMesActual() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();
        return gastoRepository.findByMesAndAnioAndUsuarioId(today.getYear(), today.getMonthValue(), usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GastoResponseDTO> obtenerProximosGastosTarjeta() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate inicioMesActual = LocalDate.now().withDayOfMonth(1);
        return gastoRepository.findProximosGastosTarjeta(usuario.getId(), inicioMesActual)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteGastoDTO> obtenerReporte(String periodo) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();
        if ("mes".equalsIgnoreCase(periodo)) {
            return gastoRepository.reportByDayAndUsuarioId(today.getYear(), today.getMonthValue(), usuario.getId()).stream()
                    .map(obj -> new ReporteGastoDTO(obj[0].toString(), (BigDecimal) obj[1]))
                    .collect(Collectors.toList());
        } else if ("anio".equalsIgnoreCase(periodo)) {
            return gastoRepository.reportByMonthAndUsuarioId(today.getYear(), usuario.getId()).stream()
                    .map(obj -> new ReporteGastoDTO(obj[0].toString(), (BigDecimal) obj[1]))
                    .collect(Collectors.toList());
        }
        return gastoRepository.reportByDayAndUsuarioId(today.getYear(), today.getMonthValue(), usuario.getId()).stream()
                .map(obj -> new ReporteGastoDTO(obj[0].toString(), (BigDecimal) obj[1]))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarGasto(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado con id: " + id));
        
        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a este gasto");
        }

        if (gasto.isPagado()) {
            actualizarPatrimonio(gasto.getPeriodoFinanciero().getMonthValue(), gasto.getPeriodoFinanciero().getYear(), gasto.getMonto().negate(), gasto.getCategoria().getTipo(), usuario);
        }
        gastoRepository.delete(gasto);
    }

    @Override
    @Transactional
    public void pagarGasto(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado con id: " + id));
        
        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado a este gasto");
        }

        if (!gasto.isPagado()) {
            gasto.setPagado(true);
            gasto.setPeriodoFinanciero(LocalDate.now().withDayOfMonth(1));
            gastoRepository.save(gasto);
            
            LocalDate now = LocalDate.now();
            actualizarPatrimonio(now.getMonthValue(), now.getYear(), gasto.getMonto(), gasto.getCategoria().getTipo(), usuario);
        }
    }

    @Override
    @Transactional
    public void pagarResumenTarjeta(String periodoStr) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        String[] parts = periodoStr.split("-");
        int anio = Integer.parseInt(parts[0]);
        int mes = Integer.parseInt(parts[1]);
        Long tarjetaId = null;
        if (parts.length > 2) {
            tarjetaId = Long.parseLong(parts[2]);
        }
        
        List<Gasto> gastos;
        if (tarjetaId != null) {
            gastos = gastoRepository.findTarjetasPendientesPorPeriodoYTarjeta(usuario.getId(), anio, mes, tarjetaId);
        } else {
            gastos = gastoRepository.findTarjetasPendientesPorPeriodo(usuario.getId(), anio, mes);
        }
        
        if (gastos.isEmpty()) {
            return;
        }

        LocalDate now = LocalDate.now();
        for (Gasto g : gastos) {
            g.setPagado(true);
            g.setPeriodoFinanciero(now.withDayOfMonth(1));
            gastoRepository.save(g);
            actualizarPatrimonio(g.getPeriodoFinanciero().getMonthValue(), g.getPeriodoFinanciero().getYear(), g.getMonto(), g.getCategoria().getTipo(), usuario);
        }
    }

    @Override
    public List<GastoResponseDTO> obtenerHistorialTarjetasPagadas() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        return gastoRepository.findHistorialTarjetasPagadas(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private GastoResponseDTO mapToDTO(Gasto gasto) {
        GastoResponseDTO dto = new GastoResponseDTO();
        dto.setId(gasto.getId());
        dto.setDescripcion(gasto.getDescripcion());
        dto.setMonto(gasto.getMonto());
        dto.setFecha(gasto.getFecha());
        dto.setMetodoPago(gasto.getMetodoPago());
        dto.setEsRecurrente(gasto.isEsRecurrente());
        dto.setPagado(gasto.isPagado());
        dto.setNotas(gasto.getNotas());
        dto.setEntidadPago(gasto.getEntidadPago());
        dto.setPeriodoFinanciero(gasto.getPeriodoFinanciero());
        if (gasto.getTarjetaCredito() != null) {
            dto.setTarjetaCreditoId(gasto.getTarjetaCredito().getId());
            dto.setNombreTarjeta(gasto.getTarjetaCredito().getNombre());
        }
        
        CategoriaResponseDTO catDto = new CategoriaResponseDTO();
        catDto.setId(gasto.getCategoria().getId());
        catDto.setNombre(gasto.getCategoria().getNombre());
        catDto.setIcono(gasto.getCategoria().getIcono());
        catDto.setTipo(gasto.getCategoria().getTipo());
        catDto.setLimiteMensual(gasto.getCategoria().getLimiteMensual());
        dto.setCategoria(catDto);

        return dto;
    }
}
