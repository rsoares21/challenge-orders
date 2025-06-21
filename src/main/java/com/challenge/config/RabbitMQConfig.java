package com.challenge.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "order-queue";

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true); // durable queue
    }
}
