package com.kevin.client_service.service.Impl;

import com.kevin.client_service.dto.ClientDto;
import com.kevin.client_service.exception.CustomException;
import com.kevin.client_service.mappers.ClientMapper;
import com.kevin.client_service.repository.ClientRepository;
import com.kevin.client_service.service.IClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class ClienteServiceImpl implements IClientService {
    private final ClientRepository clientRepository;
    @Override
    public Mono<ClientDto> getClientById(String clientId) {
        return clientRepository.findById(clientId)
                .doOnNext(client -> log.info("Cliente encontrado en la base de datos: {}", client))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Cliente con ID {} no encontrado.", clientId);
                    return Mono.error(new CustomException("Cliente no encontrado", 404, 404));
                }))
                .flatMap(client -> {
                    return clientRepository.findByClientIdAndIsActive(clientId, true)
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Cliente con ID {} está inactivo.", clientId);
                                return Mono.error(new CustomException("Cliente inactivo", 403, 403));
                            }));
                })
                .map(ClientMapper.INSTANCE::toDto)
                .doOnSuccess(clientDto -> log.info("Cliente encontrado y validado con éxito: {}", clientDto))
                .doOnError(error -> log.error("Error al obtener cliente con ID {}: {}", clientId, error.getMessage(), error));
    }
}
