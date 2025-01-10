package com.kevin.product_service.dto;

public record ProductDto(
        String productId,
        String name,
        Double price
) {}
