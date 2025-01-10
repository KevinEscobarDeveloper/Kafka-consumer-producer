package com.kevin.product_service.service;

import com.kevin.product_service.models.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IProductService {
    /**
     * Retorna un Flux<Product> con los productos de la lista de IDs.
     */
    Mono<List<Product>> findByIds(List<String> productIds);

    /**
     * Guarda un nuevo producto en la colección.
     */
    Mono<Product> saveProduct(Product product);
}
