package com.kevin.client_service.service;

import com.kevin.client_service.dto.ClientDto;
import com.kevin.client_service.models.Client;
import reactor.core.publisher.Mono;

public interface IClientService {
    Mono<ClientDto> getClientById(String clientId);
}
