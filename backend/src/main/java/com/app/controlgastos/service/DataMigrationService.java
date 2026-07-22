package com.app.controlgastos.service;

import com.app.controlgastos.model.*;
import com.app.controlgastos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataMigrationService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GastoRepository gastoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private PatrimonioMensualRepository patrimonioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateData() {
        // Encontrar datos huérfanos sin usuario
        List<Categoria> categoriasHuerfanas = categoriaRepository.findAll().stream()
                .filter(c -> c.getUsuario() == null)
                .toList();
        
        List<Gasto> gastosHuerfanos = gastoRepository.findAll().stream()
                .filter(g -> g.getUsuario() == null)
                .toList();

        List<PatrimonioMensual> patrimoniosHuerfanos = patrimonioRepository.findAll().stream()
                .filter(p -> p.getUsuario() == null)
                .toList();

        if (categoriasHuerfanas.isEmpty() && gastosHuerfanos.isEmpty() && patrimoniosHuerfanos.isEmpty()) {
            return;
        }

        Usuario admin = usuarioRepository.findByEmail("admin@ctrlgastos.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setEmail("admin@ctrlgastos.com");
                    u.setPassword(passwordEncoder.encode("admin123"));
                    u.setNombre("Administrador");
                    u.setActivo(true);
                    u.setRol(Rol.ROLE_ADMIN);
                    return usuarioRepository.save(u);
                });

        categoriasHuerfanas.forEach(c -> c.setUsuario(admin));
        gastosHuerfanos.forEach(g -> g.setUsuario(admin));
        patrimoniosHuerfanos.forEach(p -> p.setUsuario(admin));

        categoriaRepository.saveAll(categoriasHuerfanas);
        gastoRepository.saveAll(gastosHuerfanos);
        patrimonioRepository.saveAll(patrimoniosHuerfanos);
        
        System.out.println("==========================================");
        System.out.println("Migración de datos completada. Registros huérfanos asignados a admin@ctrlgastos.com (Pass: admin123)");
        System.out.println("==========================================");
    }
}
