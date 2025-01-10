package com.kevin.order_consumer_service.service.Impl;

import com.kevin.order_consumer_service.exceptions.RequestCustomException;
import com.kevin.order_consumer_service.service.ILockService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class LockServiceImpl implements ILockService {
    private final RedissonReactiveClient redissonReactiveClient;
    private final MeterRegistry meterRegistry;

    private static final long LOCK_WAIT_TIME_MS = 1000;
    private static final long LOCK_LEASE_TIME_MS = 30000;

    @Override
    public Mono<String> acquireLock(String clientId) {
        String lockKey = buildLockKey(clientId);
        String lockId = UUID.randomUUID().toString();
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket(lockKey);

        return bucket.trySet(lockId, LOCK_LEASE_TIME_MS, TimeUnit.MILLISECONDS)
                .doOnSubscribe(sub -> incrementCounter("lock.acquire.attempt"))
                .flatMap(acquired -> {
                    if (acquired) {
                        incrementCounter("lock.acquire.success");
                        log.info("Lock adquirido para clientId: {}", clientId);
                        return Mono.just(lockId);
                    } else {
                        incrementCounter("lock.acquire.failure");
                        log.warn("No se pudo adquirir el lock para clientId: {}", clientId);
                        return Mono.error(new RequestCustomException("No se pudo adquirir el lock", 500));
                    }
                })
                .doOnError(error -> logError("lock.acquire.error", clientId, error));
    }


    @Override
    public Mono<Void> releaseLock(String clientId, String lockId) {
        String lockKey = buildLockKey(clientId);
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket(lockKey);

        return bucket.get()
                .flatMap(currentLockId -> {
                    if (lockId.equals(currentLockId)) {
                        return bucket.delete()
                                .doOnSuccess(v -> {
                                    incrementCounter("lock.release.success");
                                    log.info("Lock liberado para clientId: {}", clientId);
                                });
                    } else {
                        log.warn("El lock no pertenece al cliente: {}", clientId);
                        return Mono.error(new RequestCustomException("El lock no pertenece al cliente", 500));
                    }
                })
                .doOnError(error -> logError("lock.release.error", clientId, error))
                .then();
    }


    private String buildLockKey(String clientId) {
        return "lock:" + clientId;
    }

    private void recordLockDuration(long start) {
        long duration = System.currentTimeMillis() - start;
        meterRegistry.timer("lock.acquire.duration").record(duration, TimeUnit.MILLISECONDS);
    }

    private void incrementCounter(String metric) {
        meterRegistry.counter(metric).increment();
    }

    private void logError(String metric, String clientId, Throwable error) {
        incrementCounter(metric);
        log.error("Error para clientId {}: {}", clientId, error.getMessage());
    }
}
