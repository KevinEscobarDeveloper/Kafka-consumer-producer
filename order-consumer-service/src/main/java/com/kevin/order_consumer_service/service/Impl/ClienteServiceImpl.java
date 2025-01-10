package com.kevin.order_consumer_service.service.Impl;

import com.kevin.order_consumer_service.dto.ClientDto;
import com.kevin.order_consumer_service.exceptions.RequestCustomException;
import com.kevin.order_consumer_service.service.IClienteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ClienteServiceImpl implements IClienteService {

    private final String url;
    private final WebClient webClient;

    public ClienteServiceImpl(@Value("${webclient.host}") String host,
                              @Value("${url.api.client}") String url,
                              WebClient.Builder webClientBuilder) {
        this.url = url;
        this.webClient = webClientBuilder.baseUrl(host).build();
    }


    @Override
    public Mono<ClientDto> getClient(String clientId) {
        String generalMessage = "Get recuperar cliente";
        String requestUrl = this.url + "/" + clientId;
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(ClientDto.class)
                .timeout(Duration.ofSeconds(12), Mono.error(new RequestCustomException("Timeout en el servicio: " + generalMessage, 504)));
    }
}
