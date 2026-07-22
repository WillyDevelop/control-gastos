package com.app.controlgastos.repository;

import com.app.controlgastos.model.MetaAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuarioId(Long usuarioId);
    Optional<MetaAhorro> findByUsuarioIdAndActivaParaRedondeoTrue(Long usuarioId);
}
