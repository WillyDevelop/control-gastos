package com.app.controlgastos.repository;

import com.app.controlgastos.model.TokenRecuperacion;
import com.app.controlgastos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    Optional<TokenRecuperacion> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
