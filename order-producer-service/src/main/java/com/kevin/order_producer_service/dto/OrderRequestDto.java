package com.kevin.order_producer_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull(message = "El campo orderId no debe ser nulo")
    private String orderId;
    @NotNull(message = "El campo clientId no debe ser nulo")
    private String clientId;
    @NotNull(message = "La lista productIds no debe ser nulo")
    @Size(min = 1, message = "La lista de productos debe ser de al menos un elemento")
    private List<ProductDto> products;



}
