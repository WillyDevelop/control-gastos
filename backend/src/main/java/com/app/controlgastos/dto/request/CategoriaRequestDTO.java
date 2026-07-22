package com.app.controlgastos.dto.request;

import com.app.controlgastos.model.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CategoriaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String icono;
    
    @NotNull(message = "El tipo es obligatorio")
    private TipoCategoria tipo;
    
    private BigDecimal limiteMensual;

    public CategoriaRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }
    public BigDecimal getLimiteMensual() { return limiteMensual; }
    public void setLimiteMensual(BigDecimal limiteMensual) { this.limiteMensual = limiteMensual; }
}
