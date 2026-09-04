package com.app.controlgastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gastos")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "periodo_financiero", nullable = false)
    private LocalDate periodoFinanciero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "es_recurrente")
    private boolean esRecurrente = false;

    @Column(nullable = false)
    private boolean pagado = true;

    private String notas;

    @Column(name = "entidad_pago")
    private String entidadPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_credito_id")
    private TarjetaCredito tarjetaCredito;

    @Column(name = "destinar_ahorro")
    private boolean destinarAhorro = false;

    @Column(name = "monto_ahorro", precision = 10, scale = 2)
    private BigDecimal montoAhorro;

    public Gasto() {}

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

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

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

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TarjetaCredito getTarjetaCredito() { return tarjetaCredito; }
    public void setTarjetaCredito(TarjetaCredito tarjetaCredito) { this.tarjetaCredito = tarjetaCredito; }

    public boolean isDestinarAhorro() { return destinarAhorro; }
    public void setDestinarAhorro(boolean destinarAhorro) { this.destinarAhorro = destinarAhorro; }

    public BigDecimal getMontoAhorro() { return montoAhorro; }
    public void setMontoAhorro(BigDecimal montoAhorro) { this.montoAhorro = montoAhorro; }
}
