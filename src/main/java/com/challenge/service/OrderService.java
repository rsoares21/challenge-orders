package com.challenge.service;

import com.challenge.model.Order;

import java.util.List;

public interface OrderService {
    Order saveOrder(Order order);
    double calculateTotalValue(Order order);
    List<Order> getNewOrders();
}
