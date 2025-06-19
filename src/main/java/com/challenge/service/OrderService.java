package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;

import java.util.List;

public interface OrderService {
    double calculateTotalValue(Order order);
    void addOrder(Order order);
    boolean isDuplicate(Order order);

    void updateOrderStatus(String orderId, String status);
    String getOrderStatus(String orderId);

    void updateOrderStatus(String orderId, OrderStatus status);
    OrderStatus getOrderStatusEnum(String orderId);

    List<Order> getAllOrders();
    Order saveOrder(Order order);
}
