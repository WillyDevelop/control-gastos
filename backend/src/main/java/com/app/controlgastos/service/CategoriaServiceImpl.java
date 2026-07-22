package com.app.controlgastos.service;

import com.app.controlgastos.dto.response.CategoriaResponseDTO;
import com.app.controlgastos.dto.response.PresupuestoCategoriaDTO;
import com.app.controlgastos.model.Categoria;
import com.app.controlgastos.model.Gasto;
import com.app.controlgastos.model.MetodoPago;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.CategoriaRepository;
import com.app.controlgastos.repository.GastoRepository;
import com.app.controlgastos.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final GastoRepository gastoRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, GastoRepository gastoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.gastoRepository = gastoRepository;
    }

    @Override
    public List<CategoriaResponseDTO> obtenerTodas() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        List<Categoria> categorias = categoriaRepository.findByUsuarioId(usuario.getId());
        if (categorias.isEmpty()) {
            categorias = inicializarCategoriasPorDefecto(usuario);
        }
        return categorias.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private List<Categoria> inicializarCategoriasPorDefecto(Usuario usuario) {
        Categoria cat1 = new Categoria();
        cat1.setNombre("Sueldo / Ingresos");
        cat1.setTipo(com.app.controlgastos.model.TipoCategoria.INGRESO);
        cat1.setIcono("");
        cat1.setUsuario(usuario);

        Categoria cat2 = new Categoria();
        cat2.setNombre("Supermercado / Comida");
        cat2.setTipo(com.app.controlgastos.model.TipoCategoria.GASTO);
        cat2.setIcono("");
        cat2.setUsuario(usuario);

        Categoria cat3 = new Categoria();
        cat3.setNombre("Servicios / Hogar");
        cat3.setTipo(com.app.controlgastos.model.TipoCategoria.GASTO);
        cat3.setIcono("");
        cat3.setUsuario(usuario);

        Categoria cat4 = new Categoria();
        cat4.setNombre("Varios");
        cat4.setTipo(com.app.controlgastos.model.TipoCategoria.GASTO);
        cat4.setIcono("");
        cat4.setUsuario(usuario);

        return categoriaRepository.saveAll(List.of(cat1, cat2, cat3, cat4));
    }

    @Override
    public CategoriaResponseDTO crearCategoria(com.app.controlgastos.dto.request.CategoriaRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setIcono(dto.getIcono());
        categoria.setTipo(dto.getTipo());
        categoria.setLimiteMensual(dto.getLimiteMensual());
        categoria.setUsuario(usuario);
        return mapToDTO(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, com.app.controlgastos.dto.request.CategoriaRequestDTO dto) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        categoria.setNombre(dto.getNombre());
        categoria.setIcono(dto.getIcono());
        categoria.setTipo(dto.getTipo());
        categoria.setLimiteMensual(dto.getLimiteMensual());
        return mapToDTO(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminarCategoria(Long id) {
        Usuario usuario = SecurityUtils.getCurrentUser();
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acceso denegado");
        }

        try {
            categoriaRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar la categoría porque está siendo usada por uno o más gastos.");
        }
    }

    @Override
    public List<PresupuestoCategoriaDTO> obtenerPresupuestosDelMes() {
        Usuario usuario = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();
        int anio = today.getYear();
        int mes = today.getMonthValue();

        List<Categoria> categoriasConLimite = categoriaRepository.findByUsuarioId(usuario.getId()).stream()
                .filter(cat -> cat.getLimiteMensual() != null && cat.getLimiteMensual().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        List<Gasto> gastosDelMes = gastoRepository.findByMesAndAnioAndUsuarioId(anio, mes, usuario.getId());

        return categoriasConLimite.stream().map(cat -> {
            BigDecimal montoGastado = gastosDelMes.stream()
                    .filter(g -> g.getCategoria().getId().equals(cat.getId()))
                    .filter(g -> g.getMetodoPago() != MetodoPago.TARJETA_CREDITO || g.isPagado())
                    .map(Gasto::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double porcentaje = 0;
            if (cat.getLimiteMensual().compareTo(BigDecimal.ZERO) > 0) {
                porcentaje = montoGastado.divide(cat.getLimiteMensual(), 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            }

            PresupuestoCategoriaDTO dto = new PresupuestoCategoriaDTO();
            dto.setCategoriaId(cat.getId());
            dto.setNombreCategoria(cat.getNombre());
            dto.setLimiteMensual(cat.getLimiteMensual());
            dto.setMontoGastado(montoGastado);
            dto.setPorcentajeConsumido(porcentaje);
            return dto;
        }).collect(Collectors.toList());
    }

    private CategoriaResponseDTO mapToDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setIcono(categoria.getIcono());
        dto.setTipo(categoria.getTipo());
        dto.setLimiteMensual(categoria.getLimiteMensual());
        return dto;
    }
}
