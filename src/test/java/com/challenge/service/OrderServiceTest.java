package com.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.challenge.model.Order;
import com.challenge.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    public void setup() {
        orderService = new OrderServiceImpl();
    }

    @Test
    public void testCalculateTotalValue() {
        Product p1 = new Product();
        p1.setName("Product1");
        p1.setPrice(10.0);
        Product p2 = new Product();
        p2.setName("Product2");
        p2.setPrice(20.0);

        Order order = new Order("order1", Arrays.asList(p1, p2));

        double total = orderService.calculateTotalValue(order);

        assertEquals(30.0, total, 0.001);
    }

    @Test
    public void testDuplicateOrderDetection() {
        Product prod1 = new Product();
        prod1.setName("Product1");
        prod1.setPrice(10.0);
        Order order1 = new Order("order1", Arrays.asList(prod1));
        Product prod2 = new Product();
        prod2.setName("Product2");
        prod2.setPrice(20.0);
        Order order2 = new Order("order1", Arrays.asList(prod2));

        orderService.addOrder(order1);
        boolean isDuplicate = orderService.isDuplicate(order2);

        assertTrue(isDuplicate);
    }

    @Test
    public void testOrderStatusManagement() {
        Product prod3 = new Product();
        prod3.setName("Product3");
        prod3.setPrice(15.0);
        Order order = new Order("order2", Arrays.asList(prod3));
        orderService.addOrder(order);

        orderService.updateOrderStatus(order.getOrderId(), "PROCESSING");
        String status = orderService.getOrderStatus(order.getOrderId());

        assertEquals("PROCESSING", status);

        // Test enum based status
        orderService.updateOrderStatus(order.getOrderId(), com.challenge.model.OrderStatus.PROCESSING);
        com.challenge.model.OrderStatus enumStatus = orderService.getOrderStatusEnum(order.getOrderId());

        assertEquals(com.challenge.model.OrderStatus.PROCESSING, enumStatus);
    }

    @Test
    public void testEmptyOrderTotalValue() {
        Order emptyOrder = new Order("emptyOrder", Arrays.asList());
        double total = orderService.calculateTotalValue(emptyOrder);
        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testProductWithZeroPrice() {
        Product freeProduct = new Product();
        freeProduct.setName("FreeProduct");
        freeProduct.setPrice(0.0);
        Order order = new Order("orderWithFreeProduct", Arrays.asList(freeProduct));
        double total = orderService.calculateTotalValue(order);
        assertEquals(0.0, total, 0.001);
    }
}
