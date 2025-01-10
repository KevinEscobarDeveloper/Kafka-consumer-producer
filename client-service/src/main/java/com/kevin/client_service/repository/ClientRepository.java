package com.kevin.client_service.repository;

import com.kevin.client_service.models.Client;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
    Mono<Client> findByClientIdAndIsActive(String clientId, Boolean isActive);
}
