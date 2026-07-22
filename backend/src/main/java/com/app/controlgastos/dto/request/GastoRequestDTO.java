package com.app.controlgastos.dto.request;

import com.app.controlgastos.model.MetodoPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoRequestDTO {
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    private boolean esRecurrente = false;
    private boolean pagado = true;
    private String notas;
    private String entidadPago;

    private boolean dividirGasto = false;
    private String emailDeudor;
    private BigDecimal porcentajeDeuda;
    private Long tarjetaCreditoId;

    public GastoRequestDTO() {}

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
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
    public boolean isDividirGasto() { return dividirGasto; }
    public void setDividirGasto(boolean dividirGasto) { this.dividirGasto = dividirGasto; }
    public String getEmailDeudor() { return emailDeudor; }
    public void setEmailDeudor(String emailDeudor) { this.emailDeudor = emailDeudor; }
    public BigDecimal getPorcentajeDeuda() { return porcentajeDeuda; }
    public void setPorcentajeDeuda(BigDecimal porcentajeDeuda) { this.porcentajeDeuda = porcentajeDeuda; }
    public Long getTarjetaCreditoId() { return tarjetaCreditoId; }
    public void setTarjetaCreditoId(Long tarjetaCreditoId) { this.tarjetaCreditoId = tarjetaCreditoId; }
}
