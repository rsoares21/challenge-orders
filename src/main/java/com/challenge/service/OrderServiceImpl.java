package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final Set<String> orderIds = Collections.synchronizedSet(new HashSet<>());
    private final ConcurrentMap<String, OrderStatus> orderStatusEnumMap = new ConcurrentHashMap<>();
    private final List<Order> orders = new CopyOnWriteArrayList<>();

    @Override
    public double calculateTotalValue(Order order) {
        if (order.getProducts() == null) {
            return 0.0;
        }
        return order.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    @Override
    public void addOrder(Order order) {
        if (order == null || order.getOrderId() == null) {
            throw new NullPointerException("Order or Order ID cannot be null");
        }
        synchronized (orderIds) {
            if (orderIds.contains(order.getOrderId())) {
                throw new IllegalArgumentException("Duplicate order ID: " + order.getOrderId());
            }
            orderIds.add(order.getOrderId());
        }
        orders.add(order);
    }

    @Override
    public boolean isDuplicate(Order order) {
        synchronized (orderIds) {
            return orderIds.contains(order.getOrderId());
        }
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        orderStatusEnumMap.put(orderId, status);
    }

    @Override
    public OrderStatus getOrderStatusEnum(String orderId) {
        return orderStatusEnumMap.get(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public Order saveOrder(Order order) {
        addOrder(order);
        return order;
    }

    @Override
    public void updateOrderStatusInOrder(Order order, OrderStatus status) {
        order.setOrderStatus(status);
    }

    @Override
    public OrderStatus getOrderStatusFromOrder(Order order) {
        return order.getOrderStatus();
    }
}
