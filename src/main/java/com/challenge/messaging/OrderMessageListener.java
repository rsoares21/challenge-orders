package com.challenge.messaging;

import com.challenge.config.RabbitMQConfig;
import com.challenge.model.Order;
import com.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderMessageListener(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void receiveOrderMessage(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            orderService.saveOrder(order);
            System.out.println("Received and processed order: " + order.getOrderId());
        } catch (Exception e) {
            System.err.println("Failed to process order message: " + e.getMessage());
            // Add error handling, logging, or dead-letter queue logic here
        }
    }
}
