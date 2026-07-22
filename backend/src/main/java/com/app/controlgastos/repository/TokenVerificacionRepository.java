package com.app.controlgastos.repository;

import com.app.controlgastos.model.TokenVerificacion;
import com.app.controlgastos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {
    Optional<TokenVerificacion> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
