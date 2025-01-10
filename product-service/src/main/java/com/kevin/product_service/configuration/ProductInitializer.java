package com.kevin.product_service.configuration;

import com.kevin.product_service.models.Product;
import com.kevin.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
@Slf4j
public class ProductInitializer {

    @Bean
    public ApplicationRunner initializeProducts(ProductRepository productRepository) {
        return args -> {
            productRepository.count()
                    .flatMapMany(count -> {
                        if (count > 0) {
                            log.info("Ya existen productos en la base de datos, no se generarán datos de prueba.");
                            return Flux.empty();
                        }
                        log.info("No se encontraron productos en la base de datos. Generando datos de prueba...");
                        List<Product> products = generatePredefinedProducts();
                        return productRepository.saveAll(products);
                    })
                    .doOnNext(product -> log.info("Producto guardado: {}", product))
                    .doOnComplete(() -> log.info("Productos iniciales generados y guardados exitosamente."))
                    .doOnError(error -> log.error("Error al guardar productos iniciales: {}", error.getMessage()))
                    .subscribe();
        };
    }

    private List<Product> generatePredefinedProducts() {
        return List.of(
                Product.builder()
                        .productId("P001")
                        .name("Laptop")
                        .price(1200.99)
                        .build(),
                Product.builder()
                        .productId("P002")
                        .name("Iphone")
                        .price(899.49)
                        .build(),
                Product.builder()
                        .productId("P003")
                        .name("Audifonos")
                        .price(199.99)
                        .build(),
                Product.builder()
                        .productId("P004")
                        .name("Silla Gamer")
                        .price(350.75)
                        .build(),
                Product.builder()
                        .productId("P005")
                        .name("Teclado")
                        .price(129.95)
                        .build(),
                Product.builder()
                        .productId("P006")
                        .name("Monitor")
                        .price(499.89)
                        .build(),
                Product.builder()
                        .productId("P007")
                        .name("Disco Duro")
                        .price(149.99)
                        .build(),
                Product.builder()
                        .productId("P008")
                        .name("Cargador")
                        .price(249.99)
                        .build(),
                Product.builder()
                        .productId("P009")
                        .name("Camara")
                        .price(799.00)
                        .build(),
                Product.builder()
                        .productId("P010")
                        .name("Playera")
                        .price(699.50)
                        .build()
        );
    }
}
