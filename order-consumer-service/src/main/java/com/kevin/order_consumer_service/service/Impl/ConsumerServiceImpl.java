package com.kevin.order_consumer_service.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.order_consumer_service.dto.ClientDto;
import com.kevin.order_consumer_service.dto.OrderDto;
import com.kevin.order_consumer_service.dto.ProductDto;
import com.kevin.order_consumer_service.exceptions.RequestCustomException;
import com.kevin.order_consumer_service.models.Order;
import com.kevin.order_consumer_service.repository.OrderRepository;
import com.kevin.order_consumer_service.service.IClienteService;
import com.kevin.order_consumer_service.service.IConsumerService;
import com.kevin.order_consumer_service.service.ILockService;
import com.kevin.order_consumer_service.service.IProductService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ConsumerServiceImpl implements IConsumerService {

    private final OrderRepository orderRepository;
    private final IClienteService clientService;
    private final IProductService productService;
    private final ObjectMapper mapper;
    private final ILockService lockService;
    private final MeterRegistry meterRegistry;

    @Override
    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void kafkaConsumer(ConsumerRecord<String, String> record) {
        Mono.fromCallable(() -> mapper.readValue(record.value(), OrderDto.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::handleOrder)
                .doOnSuccess(v -> log.info("Orden procesada correctamente"))
                .doOnError(error -> log.error("Error procesando orden: {}", error.getMessage()))
                .subscribe();
    }


    private Mono<Void> handleOrder(OrderDto orderDto) {
        Instant start = Instant.now();
        String clientId = orderDto.getClientId();

        int maxRetries = 3;
        long maxProcessingTime = 20000; // Tiempo máximo para todo el procesamiento en milisegundos

        Mono<String> lockMono = lockService.acquireLock(clientId)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(2))
                        .jitter(0.75)
                        .doBeforeRetry(retrySignal ->
                                log.warn("Reintento {} para adquirir lock para clientId: {}, error: {}",
                                        retrySignal.totalRetries() + 1, clientId, retrySignal.failure().getMessage())
                        )
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new RequestCustomException("No se pudo adquirir el lock después de múltiples intentos", 500)
                        )
                );

        return lockMono
                .flatMap(lockId -> processOrder(orderDto)
                        .doOnSuccess(v -> {
                            Instant end = Instant.now();

                            assert meterRegistry != null;
                            meterRegistry.timer("order.duration")
                                    .record(Duration.between(start, end));
                        })
                        .then(lockService.releaseLock(clientId, lockId))
                )
                .timeout(Duration.ofMillis(maxProcessingTime))
                .doOnError(error -> {
                    log.error("Error procesando la orden {}: {}", orderDto.getOrderId(), error.getMessage());
                });
    }



    private Mono<Void> processOrder(OrderDto orderDTO) {
        List<String> productsIds = orderDTO.getProducts().stream().map(ProductDto::productId).toList();
        log.info("Procesando orden {} para productos: {}", orderDTO.getOrderId(), productsIds);
        return Mono.zip(
                        productService.getProductsbyIds(productsIds)
                                .doOnNext(products -> log.info("Productos obtenidos: {}", products)),
                        clientService.getClient(orderDTO.getClientId())
                                .doOnNext(client -> log.info("Cliente obtenido: {}", client))
                )
                .flatMap(tuple -> saveOrder(orderDTO, tuple.getT1(), tuple.getT2()))
                .doOnSuccess(v -> log.info("Orden {} guardada exitosamente", orderDTO.getOrderId()))
                .doOnError(error -> log.error("Error al guardar la orden {}: {}", orderDTO.getOrderId(), error.getMessage()));
    }

    private Mono<Void> saveOrder(OrderDto orderDTO, List<ProductDto> products, ClientDto client) {
        Order order = new Order(orderDTO.getOrderId(), orderDTO.getClientId(), client.name(), products);
        meterRegistry.counter("order.saved").increment();
        return orderRepository.save(order).then();
    }

}
