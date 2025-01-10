package com.kevin.order_consumer_service.dto;

public record ProductDto(
        String productId,
        String name,
        Double price
) {}
