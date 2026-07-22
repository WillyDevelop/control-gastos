package com.app.controlgastos.repository;

import com.app.controlgastos.model.TarjetaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {
    List<TarjetaCredito> findByUsuarioId(Long usuarioId);
}
