package com.kevin.order_consumer_service.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumerService {
    public void kafkaConsumer(ConsumerRecord<String, String> record);
}
