package com.kevin.client_service.config;

import com.kevin.client_service.models.Client;
import com.kevin.client_service.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
@Slf4j
public class ClientInitializer {

    @Bean
    public ApplicationRunner initializeProducts(ClientRepository clientRepository) {
        return args -> {
            clientRepository.count()
                    .flatMapMany(count -> {
                        if (count > 0) {
                            log.info("Ya existen clientes en la base de datos, no se generarán datos de prueba.");
                            return Flux.empty();
                        }
                        log.info("No se encontraron clientes en la base de datos. Generando datos de prueba...");
                        List<Client> clientes = generatePredefinedProducts();
                        return clientRepository.saveAll(clientes);
                    })
                    .doOnNext(product -> log.info("Producto guardado: {}", product))
                    .doOnComplete(() -> log.info("Productos iniciales generados y guardados exitosamente."))
                    .doOnError(error -> log.error("Error al guardar productos iniciales: {}", error.getMessage()))
                    .subscribe();
        };
    }

    private List<Client> generatePredefinedProducts() {
        return List.of(
                Client.builder()
                        .clientId("C001")
                        .name("Kevin Escobar")
                        .address("Mexico")
                        .isActive(true)
                        .purchaseHistory(List.of("P001", "P002"))
                        .build(),
                Client.builder()
                        .clientId("C002")
                        .name("Alberto Escobar")
                        .address("Mexico")
                        .isActive(false)
                        .purchaseHistory(List.of("P003"))
                        .build()
        );
    }
}
