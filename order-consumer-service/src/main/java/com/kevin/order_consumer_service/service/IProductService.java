package com.kevin.order_consumer_service.service;

import com.kevin.order_consumer_service.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IProductService {
    public Mono<List<ProductDto>> getProductsbyIds(List<String> productIds);
}
