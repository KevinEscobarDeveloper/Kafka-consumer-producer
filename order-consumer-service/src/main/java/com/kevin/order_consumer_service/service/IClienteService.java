package com.kevin.order_consumer_service.service;

import com.kevin.order_consumer_service.dto.ClientDto;
import reactor.core.publisher.Mono;

public interface IClienteService {
    Mono<ClientDto> getClient(String clientId);
}
