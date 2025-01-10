package com.kevin.order_consumer_service.models;

import com.kevin.order_consumer_service.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String orderId;
    private String customerId;
    private String customerName;
    private List<ProductDto> products;
}
