package com.kevin.order_consumer_service.service.Impl;

import com.kevin.order_consumer_service.dto.ProductDto;
import com.kevin.order_consumer_service.exceptions.RequestCustomException;
import com.kevin.order_consumer_service.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {
    private final String url;
    private final WebClient webClient;

    public ProductServiceImpl(@Value("${webclient.host}") String host,
                              @Value("${url.api.product}") String url,
                              WebClient.Builder webClientBuilder) {
        this.url = url;
        this.webClient = webClientBuilder.baseUrl(host).build();
    }
    @Override
    public Mono<List<ProductDto>> getProductsbyIds(List<String> productIds) {
        String generalMessage = "Get recuperar cliente";
        String queryParams = productIds.stream()
                .map(id -> "ids=" + id)
                .collect(Collectors.joining("&"));
        String requestUrl = url +"?"+ queryParams;
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                })
                .timeout(Duration.ofSeconds(15), Mono.error(new RequestCustomException("Timeout en el servicio: " + generalMessage, 504)))
                .doOnError(e -> log.error("Error al consultar productos: {}", e.getMessage()));
    }
}
