package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import com.challenge.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order saveOrder(Order order) {

        if (order == null || order.getOrderId() == null) {
            throw new NullPointerException("Order or Order ID cannot be null");
        }
        if (isDuplicate(order)) {
            throw new IllegalArgumentException("Duplicate order ID: " + order.getOrderId());
        }
        double totalValue = calculateTotalValue(order);
        order.setTotalValue(totalValue);
        order.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order);

        return order;
    }

    @Override
    public boolean isDuplicate(Order order) {
        if (order == null || order.getOrderId() == null) {
            return false;
        }
        Optional<Order> existingOrder = orderRepository.findById(order.getOrderId());
        return existingOrder.isPresent();
    }

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
    public List<Order> getNewOrders() {
        return orderRepository.findByOrderStatus(OrderStatus.NEW);
    }

}
