package com.app.controlgastos.repository;

import com.app.controlgastos.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    
    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.usuario.id = :usuarioId")
    BigDecimal sumMontoByMesAndAnioAndUsuarioId(@Param("anio") int anio, @Param("mes") int mes, @Param("usuarioId") Long usuarioId);

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.pagado = true AND g.categoria.tipo = 'GASTO' AND g.usuario.id = :usuarioId")
    BigDecimal sumPagadoByMesAndAnioAndUsuarioId(@Param("anio") int anio, @Param("mes") int mes, @Param("usuarioId") Long usuarioId);

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.pagado = true AND g.categoria.tipo = 'INGRESO' AND g.usuario.id = :usuarioId")
    BigDecimal sumIngresosByMesAndAnioAndUsuarioId(@Param("anio") int anio, @Param("mes") int mes, @Param("usuarioId") Long usuarioId);

    @Query("SELECT g FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.usuario.id = :usuarioId ORDER BY g.fecha DESC")
    List<Gasto> findByMesAndAnioAndUsuarioId(@Param("anio") int anio, @Param("mes") int mes, @Param("usuarioId") Long usuarioId);

    @Query("SELECT g.fecha, SUM(g.monto) FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.usuario.id = :usuarioId GROUP BY g.fecha ORDER BY g.fecha")
    List<Object[]> reportByDayAndUsuarioId(@Param("anio") int anio, @Param("mes") int mes, @Param("usuarioId") Long usuarioId);

    @Query("SELECT MONTH(g.periodoFinanciero), SUM(g.monto) FROM Gasto g WHERE YEAR(g.periodoFinanciero) = :anio AND g.usuario.id = :usuarioId GROUP BY MONTH(g.periodoFinanciero) ORDER BY MONTH(g.periodoFinanciero)")
    List<Object[]> reportByMonthAndUsuarioId(@Param("anio") int anio, @Param("usuarioId") Long usuarioId);

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.metodoPago = 'TARJETA_CREDITO' AND g.pagado = false AND g.periodoFinanciero > :inicioMesActual ORDER BY g.fecha ASC")
    List<Gasto> findProximosGastosTarjeta(@Param("usuarioId") Long usuarioId, @Param("inicioMesActual") java.time.LocalDate inicioMesActual);

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.metodoPago = 'TARJETA_CREDITO' AND g.pagado = false AND YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes")
    java.math.BigDecimal sumTarjetasPendientesPorMes(@Param("usuarioId") Long usuarioId, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.metodoPago = 'TARJETA_CREDITO' AND g.pagado = false AND YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes")
    List<Gasto> findTarjetasPendientesPorPeriodo(@Param("usuarioId") Long usuarioId, @Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.metodoPago = 'TARJETA_CREDITO' AND g.pagado = false AND YEAR(g.periodoFinanciero) = :anio AND MONTH(g.periodoFinanciero) = :mes AND g.tarjetaCredito.id = :tarjetaId")
    List<Gasto> findTarjetasPendientesPorPeriodoYTarjeta(@Param("usuarioId") Long usuarioId, @Param("anio") int anio, @Param("mes") int mes, @Param("tarjetaId") Long tarjetaId);

    @Query("SELECT g FROM Gasto g WHERE g.usuario.id = :usuarioId AND g.metodoPago = 'TARJETA_CREDITO' AND g.pagado = true ORDER BY g.fecha DESC")
    List<Gasto> findHistorialTarjetasPagadas(@Param("usuarioId") Long usuarioId);
}
