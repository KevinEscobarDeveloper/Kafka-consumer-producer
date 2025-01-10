package com.kevin.order_consumer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class OrderConsumerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderConsumerServiceApplication.class, args);
	}

}
