package com.app.controlgastos.repository;

import com.app.controlgastos.model.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {
    List<Deuda> findByAcreedorIdAndLiquidadaFalse(Long acreedorId);
}
