package com.challenge.controller;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import com.challenge.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    // Testa a criação de um pedido com dados válidos
    void testCreateOrder() {
        Order order = new Order();
        order.setOrderId("123");
        order.setOrderStatus(OrderStatus.NEW);

        when(orderService.saveOrder(order)).thenReturn(order);

        ResponseEntity<Order> response = orderController.createOrder(order);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(order, response.getBody());

        verify(orderService, times(1)).saveOrder(order);
    }

    @Test
    // Testa a criação de um pedido nulo, esperando uma exceção NullPointerException
    void testCreateOrder_NullOrder() {
        when(orderService.saveOrder(null)).thenThrow(new NullPointerException("Order cannot be null"));

        assertThrows(NullPointerException.class, () -> orderController.createOrder(null));

        verify(orderService, times(1)).saveOrder(null);
    }

    @Test
    // Testa a recuperação de pedidos com status "NEW"
    void testGetNewOrders() {
        Order order1 = new Order();
        order1.setOrderId("123");
        order1.setOrderStatus(OrderStatus.NEW);

        Order order2 = new Order();
        order2.setOrderId("456");
        order2.setOrderStatus(OrderStatus.NEW);

        List<Order> orders = Arrays.asList(order1, order2);

        when(orderService.getNewOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getNewOrders();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());

        verify(orderService, times(1)).getNewOrders();
    }

    @Test
    // Testa a recuperação de pedidos com status "NEW" quando a lista está vazia
    void testGetNewOrders_EmptyList() {
        when(orderService.getNewOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getNewOrders();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());

        verify(orderService, times(1)).getNewOrders();
    }
}
