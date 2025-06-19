package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;

public class OrderServiceImpl implements OrderService {

    private Set<String> orderIds = new HashSet<>();
    private ConcurrentMap<String, String> orderStatusMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, OrderStatus> orderStatusEnumMap = new ConcurrentHashMap<>();
    private List<Order> orders = new ArrayList<>();

    @Override
    public double calculateTotalValue(Order order) {
        return order.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    @Override
    public void addOrder(Order order) {
        orderIds.add(order.getOrderId());
        orders.add(order);
    }

    @Override
    public boolean isDuplicate(Order order) {
        return orderIds.contains(order.getOrderId());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        orderStatusMap.put(orderId, status);
    }

    @Override
    public String getOrderStatus(String orderId) {
        return orderStatusMap.get(orderId);
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
}
