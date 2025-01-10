package com.kevin.product_service.service.Impl;

import com.kevin.product_service.exception.CustomException;
import com.kevin.product_service.models.Product;
import com.kevin.product_service.repository.ProductRepository;
import com.kevin.product_service.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCacheReactive;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final RMapCacheReactive<String, Product> productCache;
    private final long cacheTtl;

    public ProductServiceImpl(ProductRepository productRepository, RMapCacheReactive<String, Product> productCache, long cacheTtl) {
        this.productRepository = productRepository;
        this.productCache = productCache;
        this.cacheTtl = cacheTtl;
    }

    @Override
    public Mono<List<Product>> findByIds(List<String> productIds) {
        log.info("Buscando productos por IDs: {}", productIds);

        return Flux.fromIterable(productIds)
                .flatMap(this::getProductByIdWithCache)
                .collectList()
                .flatMap(products -> {
                    if (products.size() == productIds.size()) {
                        log.info("Todos los productos existen en el caché o la base de datos.");
                        return Mono.just(products);
                    } else {
                        log.warn("Algunos productos no existen.");
                        return Mono.error(new CustomException("Algunos productos no existen en el catálogo.", 400, 400));
                    }
                });
    }

    private Mono<Product> getProductByIdWithCache(String productId) {
        return productCache.get(productId)
                .switchIfEmpty(
                        productRepository.findById(productId)
                                .flatMap(product -> productCache.put(productId, product, cacheTtl, TimeUnit.MINUTES)
                                        .thenReturn(product))
                );
    }


    @Override
    public Mono<Product> saveProduct(Product product) {
        log.info("Guardando producto: {}", product);
        return productRepository.save(product)
                .flatMap(savedProduct -> productCache.fastPut(savedProduct.getProductId(), savedProduct)
                        .thenReturn(savedProduct)); // Actualiza el caché después de guardar
    }
}
