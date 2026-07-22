package com.app.controlgastos.dto.response;

import com.app.controlgastos.model.MetodoPago;
import java.math.BigDecimal;

public class PlantillaGastoResponseDTO {
    private Long id;
    private String nombrePlantilla;
    private String descripcion;
    private BigDecimal monto;
    private CategoriaResponseDTO categoria;
    private MetodoPago metodoPago;

    public PlantillaGastoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombrePlantilla() { return nombrePlantilla; }
    public void setNombrePlantilla(String nombrePlantilla) { this.nombrePlantilla = nombrePlantilla; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public CategoriaResponseDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaResponseDTO categoria) { this.categoria = categoria; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
}
