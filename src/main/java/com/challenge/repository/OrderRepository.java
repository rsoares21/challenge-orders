package com.challenge.repository;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
}
