package com.kevin.order_producer_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.order_producer_service.dto.OrderRequestDto;
import com.kevin.order_producer_service.dto.ResponseDto;
import com.kevin.order_producer_service.exception.KafkaCustomException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class OrderProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Counter successfulOrdersCounter;
    private final Counter failedOrdersCounter;
    private final ObjectMapper objectMapper;
    @Value("${kafka.topic.orders}")
    private String topic;

    public OrderProducerService(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.successfulOrdersCounter = Counter.builder("orders.producer.success")
                .description("Número de pedidos enviados exitosamente")
                .tag("type", "success")
                .register(meterRegistry);

        this.failedOrdersCounter = Counter.builder("orders.producer.failure")
                .description("Número de pedidos enviados fallidos")
                .tag("type", "failure")
                .register(meterRegistry);
        this.objectMapper = objectMapper;
    }

    /**
     * Publica un mensaje en Kafka
     *
     * @param orderRequest El mensaje a enviar.
     * @return Mono<ResponseDto> que representa el resultado de la operación.
     */
    public Mono<ResponseDto> publishTransactionalMessage(OrderRequestDto orderRequest) {
        Instant start = Instant.now();

        return Mono.fromCallable(() -> serializeToJson(orderRequest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(orderJson -> Mono.fromFuture(kafkaTemplate.send(topic, orderRequest.getOrderId(), orderJson)))
                .timeout(Duration.ofSeconds(15))
                .flatMap(result -> {
                    Instant end = Instant.now();
                    log.info("Mensaje enviado exitosamente: topic={}, partition={}, offset={}, duration={}ms",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            Duration.between(start, end).toMillis());
                    successfulOrdersCounter.increment();

                    ResponseDto response = new ResponseDto();
                    response.setSuccess(true);
                    response.setMessage("Mensaje enviado exitosamente a Kafka");
                    return Mono.just(response);
                })
                .onErrorResume(error -> {
                    Instant end = Instant.now();
                    log.error("Error al enviar mensaje: topic={}, duration={}ms, error={}",
                            topic, Duration.between(start, end).toMillis(), error.getMessage());
                    failedOrdersCounter.increment();
                    return Mono.error(new KafkaCustomException("Kafka no disponible: " + error.getMessage(), error));
                });
    }

    private String serializeToJson(OrderRequestDto orderRequest) {
        try {
            String orderJson = objectMapper.writeValueAsString(orderRequest);
            log.info("Mensaje serializado a JSON: {}", orderJson);
            return orderJson;
        } catch (Exception e) {
            log.error("Error al serializar OrderRequestDto a JSON: {}", e.getMessage());
            throw new KafkaCustomException("Error al serializar el mensaje a JSON.", e);
        }
    }

}
