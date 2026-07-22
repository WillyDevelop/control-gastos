package com.app.controlgastos.repository;

import com.app.controlgastos.model.PlantillaGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaGastoRepository extends JpaRepository<PlantillaGasto, Long> {
    List<PlantillaGasto> findByUsuarioId(Long usuarioId);
}
