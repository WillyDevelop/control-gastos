package com.app.controlgastos.config;

import com.app.controlgastos.model.Gasto;
import com.app.controlgastos.model.MetodoPago;
import com.app.controlgastos.model.TarjetaCredito;
import com.app.controlgastos.model.Usuario;
import com.app.controlgastos.repository.GastoRepository;
import com.app.controlgastos.repository.TarjetaCreditoRepository;
import com.app.controlgastos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataMigrationConfig implements CommandLineRunner {

    private final GastoRepository gastoRepository;
    private final TarjetaCreditoRepository tarjetaRepository;
    private final UsuarioRepository usuarioRepository;

    public DataMigrationConfig(GastoRepository gastoRepository, TarjetaCreditoRepository tarjetaRepository, UsuarioRepository usuarioRepository) {
        this.gastoRepository = gastoRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Find all expenses with TARJETA_CREDITO but no TarjetaCredito assigned
        List<Gasto> orphanExpenses = gastoRepository.findAll().stream()
                .filter(g -> g.getMetodoPago() == MetodoPago.TARJETA_CREDITO && g.getTarjetaCredito() == null)
                .collect(Collectors.toList());

        if (orphanExpenses.isEmpty()) {
            return;
        }

        // Group by user
        var expensesByUser = orphanExpenses.stream().collect(Collectors.groupingBy(Gasto::getUsuario));

        for (var entry : expensesByUser.entrySet()) {
            Usuario usuario = entry.getKey();
            List<Gasto> gastos = entry.getValue();

            // Check if user already has a "Predeterminada" card
            TarjetaCredito defaultCard = tarjetaRepository.findByUsuarioId(usuario.getId()).stream()
                    .filter(t -> "Predeterminada".equals(t.getNombre()))
                    .findFirst()
                    .orElseGet(() -> {
                        TarjetaCredito t = new TarjetaCredito();
                        t.setNombre("Predeterminada");
                        t.setDiaCierre(27);
                        t.setDiaVencimiento(10);
                        t.setUsuario(usuario);
                        return tarjetaRepository.save(t);
                    });

            for (Gasto g : gastos) {
                g.setTarjetaCredito(defaultCard);
                gastoRepository.save(g);
            }
        }
        
        System.out.println("DataMigrationConfig: Migrated " + orphanExpenses.size() + " orphan credit card expenses.");
    }
}
