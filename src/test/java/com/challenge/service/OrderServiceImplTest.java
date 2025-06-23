package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import com.challenge.model.Product;
import com.challenge.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveOrder_Success() {
        Order order = new Order();
        order.setOrderId("123");
        Product product1 = new Product();
        product1.setPrice(10.0);
        Product product2 = new Product();
        product2.setPrice(20.0);
        order.setProducts(Arrays.asList(product1, product2));

        when(orderRepository.findById("123")).thenReturn(Optional.empty());
        when(orderRepository.save(order)).thenReturn(order);

        Order savedOrder = orderService.saveOrder(order);

        assertNotNull(savedOrder);
        assertEquals(30.0, savedOrder.getTotalValue());
        assertEquals(OrderStatus.NEW, savedOrder.getOrderStatus());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testSaveOrder_NullOrder_ThrowsException() {
        assertThrows(NullPointerException.class, () -> orderService.saveOrder(null));
    }

    @Test
    void testSaveOrder_NullOrderId_ThrowsException() {
        Order order = new Order();
        order.setOrderId(null);
        assertThrows(NullPointerException.class, () -> orderService.saveOrder(order));
    }

    @Test
    void testSaveOrder_DuplicateOrder_ThrowsException() {
        Order order = new Order();
        order.setOrderId("123");

        when(orderRepository.findById("123")).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class, () -> orderService.saveOrder(order));
    }

    @Test
    void testIsDuplicate_True() {
        Order order = new Order();
        order.setOrderId("123");

        when(orderRepository.findById("123")).thenReturn(Optional.of(order));

        assertTrue(orderService.isDuplicate(order));
    }

    @Test
    void testIsDuplicate_False() {
        Order order = new Order();
        order.setOrderId("123");

        when(orderRepository.findById("123")).thenReturn(Optional.empty());

        assertFalse(orderService.isDuplicate(order));
    }

    @Test
    void testCalculateTotalValue() {
        Order order = new Order();
        Product product1 = new Product();
        product1.setPrice(15.0);
        Product product2 = new Product();
        product2.setPrice(25.0);
        order.setProducts(Arrays.asList(product1, product2));

        double total = orderService.calculateTotalValue(order);

        assertEquals(40.0, total);
    }

    @Test
    void testCalculateTotalValue_NullProducts() {
        Order order = new Order();
        order.setProducts(null);

        double total = orderService.calculateTotalValue(order);

        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalValue_EmptyProducts() {
        Order order = new Order();
        order.setProducts(Collections.emptyList());

        double total = orderService.calculateTotalValue(order);

        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalValue_ZeroPriceProducts() {
        Order order = new Order();
        Product product1 = new Product();
        product1.setPrice(0.0);
        Product product2 = new Product();
        product2.setPrice(0.0);
        order.setProducts(Arrays.asList(product1, product2));

        double total = orderService.calculateTotalValue(order);

        assertEquals(0.0, total);
    }

    @Test
    void testGetNewOrders() {
        Order order1 = new Order();
        order1.setOrderId("1");
        order1.setOrderStatus(OrderStatus.NEW);

        Order order2 = new Order();
        order2.setOrderId("2");
        order2.setOrderStatus(OrderStatus.NEW);

        List<Order> newOrders = Arrays.asList(order1, order2);

        when(orderRepository.findByOrderStatus(OrderStatus.NEW)).thenReturn(newOrders);

        List<Order> result = orderService.getNewOrders();

        assertEquals(2, result.size());
        assertEquals(newOrders, result);
    }
}
