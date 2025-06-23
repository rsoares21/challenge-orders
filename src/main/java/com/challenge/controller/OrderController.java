package com.challenge.controller;

import com.challenge.model.Order;
import com.challenge.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Endpoint for external product A to submit orders
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    // Endpoint for external product B to retrieve processed orders
    @GetMapping
    public ResponseEntity<List<Order>> getNewOrders() {
        List<Order> orders = orderService.getNewOrders();
        return ResponseEntity.ok(orders);
    }

}
