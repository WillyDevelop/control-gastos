package com.app.controlgastos.dto.response;

import com.app.controlgastos.model.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoResponseDTO {
    private Long id;
    private String descripcion;
    private BigDecimal monto;
    private LocalDate fecha;
    private LocalDate periodoFinanciero;
    private MetodoPago metodoPago;
    private boolean esRecurrente;
    private boolean pagado;
    private String notas;
    private String entidadPago;
    private CategoriaResponseDTO categoria;
    private Long tarjetaCreditoId;
    private String nombreTarjeta;

    public GastoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalDate getPeriodoFinanciero() { return periodoFinanciero; }
    public void setPeriodoFinanciero(LocalDate periodoFinanciero) { this.periodoFinanciero = periodoFinanciero; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public boolean isEsRecurrente() { return esRecurrente; }
    public void setEsRecurrente(boolean esRecurrente) { this.esRecurrente = esRecurrente; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getEntidadPago() { return entidadPago; }
    public void setEntidadPago(String entidadPago) { this.entidadPago = entidadPago; }
    public CategoriaResponseDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaResponseDTO categoria) { this.categoria = categoria; }
    public Long getTarjetaCreditoId() { return tarjetaCreditoId; }
    public void setTarjetaCreditoId(Long tarjetaCreditoId) { this.tarjetaCreditoId = tarjetaCreditoId; }
    public String getNombreTarjeta() { return nombreTarjeta; }
    public void setNombreTarjeta(String nombreTarjeta) { this.nombreTarjeta = nombreTarjeta; }
}
