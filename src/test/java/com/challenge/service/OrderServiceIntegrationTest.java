package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import com.challenge.model.Product;
import com.challenge.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        // Removed manual cleanup since @Transactional will rollback after each test
    }

    @Test
    void testSaveOrderAndRetrieve() {
        Order order = new Order();
        order.setOrderId("order123");
        Product product1 = new Product();
        product1.setPrice(10.0);
        Product product2 = new Product();
        product2.setPrice(20.0);
        order.setProducts(Arrays.asList(product1, product2));

        Order savedOrder = orderService.saveOrder(order);

        assertNotNull(savedOrder);
        assertEquals(30.0, savedOrder.getTotalValue());
        assertEquals(OrderStatus.NEW, savedOrder.getOrderStatus());

        Order retrievedOrder = orderRepository.findById("order123").orElse(null);
        assertNotNull(retrievedOrder);
        assertEquals("order123", retrievedOrder.getOrderId());
    }

    @Test
    void testGetNewOrders() {
        Order order1 = new Order();
        order1.setOrderId("order1");
        order1.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setOrderId("order2");
        order2.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order2);

        List<Order> newOrders = orderService.getNewOrders();

        assertEquals(2, newOrders.size());
        assertTrue(newOrders.stream().anyMatch(o -> o.getOrderId().equals("order1")));
        assertTrue(newOrders.stream().anyMatch(o -> o.getOrderId().equals("order2")));
    }
}
