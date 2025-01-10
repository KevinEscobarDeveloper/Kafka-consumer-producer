package com.kevin.client_service.config;

import com.kevin.client_service.models.Client;
import org.redisson.Redisson;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CacheConfiguration {

    @Value("${cache.ttl:30}")
    private long cacheTtl;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }
    @Bean
    public RedissonReactiveClient redissonReactiveClient(RedissonClient redissonClient) {
        return redissonClient.reactive();
    }

    @Bean
    public RMapCacheReactive<String, Client> productCache(RedissonReactiveClient redissonReactiveClient) {
        return redissonReactiveClient.getMapCache("clients");
    }
    @Bean
    public long cacheTtl() {
        return cacheTtl;
    }

}
