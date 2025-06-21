package com.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

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

        Order order = new Order();
        order.setOrderId("order1");
        order.setProducts(Arrays.asList(p1, p2));

        double total = orderService.calculateTotalValue(order);

        assertEquals(30.0, total, 0.001);
    }

    @Test
    public void testDuplicateOrderDetection() {
        Product prod1 = new Product();
        prod1.setName("Product1");
        prod1.setPrice(10.0);
        Order order1 = new Order();
        order1.setOrderId("order1");
        order1.setProducts(Arrays.asList(prod1));
        Product prod2 = new Product();
        prod2.setName("Product2");
        prod2.setPrice(20.0);
        Order order2 = new Order();
        order2.setOrderId("order1");
        order2.setProducts(Arrays.asList(prod2));

        orderService.addOrder(order1);
        boolean isDuplicate = orderService.isDuplicate(order2);

        assertTrue(isDuplicate);
    }

    @Test
    public void testOrderStatusManagement() {
        Product prod3 = new Product();
        prod3.setName("Product3");
        prod3.setPrice(15.0);
        Order order = new Order();
        order.setOrderId("order2");
        order.setProducts(Arrays.asList(prod3));
        order.setOrderStatus(null);
        orderService.addOrder(order);

        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.PROCESSING);
        OrderStatus enumStatus = orderService.getOrderStatusEnum(order.getOrderId());

        assertEquals(OrderStatus.PROCESSING, enumStatus);

        // Test updating status in Order entity
        orderService.updateOrderStatusInOrder(order, OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());

        // Test getting status from Order entity
        OrderStatus statusFromOrder = orderService.getOrderStatusFromOrder(order);
        assertEquals(OrderStatus.COMPLETED, statusFromOrder);
    }

    @Test
    public void testEmptyOrderTotalValue() {
        Order emptyOrder = new Order();
        emptyOrder.setOrderId("emptyOrder");
        emptyOrder.setProducts(Collections.emptyList());
        double total = orderService.calculateTotalValue(emptyOrder);
        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testProductWithZeroPrice() {
        Product freeProduct = new Product();
        freeProduct.setName("FreeProduct");
        freeProduct.setPrice(0.0);
        Order order = new Order();
        order.setOrderId("orderWithFreeProduct");
        order.setProducts(Arrays.asList(freeProduct));
        double total = orderService.calculateTotalValue(order);
        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testAddNullOrder() {
        try {
            orderService.addOrder(null);
        } catch (Exception e) {
            // Expect no exception or handle gracefully
        }
    }

    @Test
    public void testCalculateTotalValue_NullProducts() {
        Order order = new Order();
        order.setOrderId("orderNullProducts");
        order.setProducts(null);
        double total = orderService.calculateTotalValue(order);
        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testUpdateOrderStatus_InvalidOrderId() {
        try {
            orderService.updateOrderStatus("invalidId", OrderStatus.PROCESSING);
        } catch (Exception e) {
            // Expect no exception or handle gracefully
        }
    }

    @Test
    public void testUpdateOrderStatus_NullStatus() {
        Product prod = new Product();
        prod.setName("Product");
        prod.setPrice(10.0);
        Order order = new Order();
        order.setOrderId("orderWithNullStatus");
        order.setProducts(Arrays.asList(prod));
        orderService.addOrder(order);

        try {
            orderService.updateOrderStatus(order.getOrderId(), null);
        } catch (Exception e) {
            // Expect no exception or handle gracefully
        }
    }

    // Additional edge case tests

    @Test
    public void testAddOrder_NullOrderId() {
        Order order = new Order();
        order.setOrderId(null);
        order.setProducts(Collections.emptyList());
        assertThrows(NullPointerException.class, () -> orderService.addOrder(order));
    }

    @Test
    public void testUpdateOrderStatus_NullOrderId() {
        assertThrows(NullPointerException.class, () -> orderService.updateOrderStatus(null, OrderStatus.PROCESSING));
    }

    @Test
    public void testConcurrentAddOrder() throws InterruptedException {
        final Order order1 = new Order();
        order1.setOrderId("concurrentOrder1");
        order1.setProducts(Collections.emptyList());

        final Order order2 = new Order();
        order2.setOrderId("concurrentOrder1"); // same ID to test duplicate detection
        order2.setProducts(Collections.emptyList());

        Thread thread1 = new Thread(() -> {
            try {
                orderService.addOrder(order1);
            } catch (Exception e) {
                // ignore
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                orderService.addOrder(order2);
            } catch (Exception e) {
                // ignore
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Only one order with the ID should be added
        int count = 0;
        for (Order o : orderService.getAllOrders()) {
            if ("concurrentOrder1".equals(o.getOrderId())) {
                count++;
            }
        }
        assertEquals(1, count);
    }
}
