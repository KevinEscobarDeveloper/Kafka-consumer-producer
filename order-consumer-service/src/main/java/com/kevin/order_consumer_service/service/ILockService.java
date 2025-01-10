package com.kevin.order_consumer_service.service;

import reactor.core.publisher.Mono;

public interface ILockService {
    public Mono<String> acquireLock(String clientId);
    public Mono<Void> releaseLock(String clientId, String lockId);
}
