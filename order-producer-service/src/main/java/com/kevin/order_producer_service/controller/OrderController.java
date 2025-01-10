package com.kevin.order_producer_service.controller;

import com.kevin.order_producer_service.dto.OrderRequestDto;
import com.kevin.order_producer_service.dto.ResponseDto;
import com.kevin.order_producer_service.service.OrderProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/orders")
@Tag(name = "Order producer Microservice", description = "Recibe Ordenes y las publica en un topico de kafka")
public class OrderController {

    private final OrderProducerService orderProducerService;

    @Operation(
            summary = "Agregar un libro y enviarlo a Kafka",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mensaje publicado exitosamente"),
            }
    )
    @PostMapping("/publish")
    public Mono<ResponseEntity<ResponseDto>> publishOrder(
            @RequestBody OrderRequestDto orderRequest) {
        log.info("OrderId: {}", orderRequest.getOrderId());

        return orderProducerService.publishTransactionalMessage( orderRequest)
                .map(result -> {
                    log.info("Mensaje publicado exitosamente: orderId={}", orderRequest.getOrderId());
                    return ResponseEntity.ok().body(result);
                });
    }
}
