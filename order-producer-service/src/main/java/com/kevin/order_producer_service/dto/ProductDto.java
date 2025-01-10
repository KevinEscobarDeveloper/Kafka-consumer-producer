package com.kevin.order_producer_service.dto;

public record ProductDto(
        String productId,
        String name,
        Double price
) {}
