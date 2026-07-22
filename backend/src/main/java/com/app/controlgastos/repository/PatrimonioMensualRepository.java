package com.app.controlgastos.repository;

import com.app.controlgastos.model.PatrimonioMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatrimonioMensualRepository extends JpaRepository<PatrimonioMensual, Long> {
    Optional<PatrimonioMensual> findByMesAndAnioAndUsuarioId(Integer mes, Integer anio, Long usuarioId);
}
