package com.challenge.controller;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import com.challenge.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        orderService = Mockito.mock(OrderService.class);
        orderController = new OrderController();
        java.lang.reflect.Field orderServiceField;
        try {
            orderServiceField = OrderController.class.getDeclaredField("orderService");
            orderServiceField.setAccessible(true);
            orderServiceField.set(orderController, orderService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Testa o endpoint createOrder para garantir que ele salva corretamente um pedido e retorna o pedido salvo com status OK.
     * Verifica se o valor total e o status do pedido são definidos corretamente e se o método saveOrder do serviço é chamado com o pedido esperado.
     */
    @Test
    public void testCreateOrder() {
        Product product1 = new Product(1L, "Product 1", 10.0);
        Product product2 = new Product(2L, "Product 2", 20.0);

        Order order = new Order();
        order.setProducts(Arrays.asList(product1, product2));
        order.setOrderStatus(OrderStatus.NEW);
        order.setTotalValue(30.0);

        when(orderService.saveOrder(any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderController.createOrder(order);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(30.0, response.getBody().getTotalValue(), 0.001);
        assertEquals(OrderStatus.NEW, response.getBody().getOrderStatus());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).saveOrder(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        assertEquals(2, capturedOrder.getProducts().size());
    }

    /**
     * Testa o comportamento do endpoint createOrder quando a lista de produtos é nula.
     * Verifica se umproceeda exceção NullPointerException é lançada.
     */
    @Test
    public void testCreateOrder_NullProductList() {
        Order order = new Order();
        order.setProducts(null);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            orderController.createOrder(order);
        });

        assertEquals("A lista de produtos não pode ser nula ou vazia.", exception.getMessage());
    }

    /**
     * Testa o endpoint getNewOrders para garantir que ele retorna uma lista de pedidos novos com status OK.
     * Verifica se o método getNewOrders do serviço é chamado e se a resposta contém o número esperado de pedidos.
     */
    @Test
    public void testGetNewOrders() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.NEW);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.NEW);

        List<Order> orders = Arrays.asList(order1, order2);

        when(orderService.getNewOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getNewOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(orderService).getNewOrders();
    }

    /**
     * Testa o comportamento do endpoint createOrder quando o serviço lança uma exceção.
     * Verifica se a exceção é propagada corretamente.
     */
    @Test
    public void testCreateOrder_ServiceThrowsException() {
        Order order = new Order();
        when(orderService.saveOrder(any(Order.class))).thenThrow(new RuntimeException("A lista de produtos não pode ser nula ou vazia."));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.createOrder(order);
        });

        assertEquals("A lista de produtos não pode ser nula ou vazia.", exception.getMessage());
    }

    /**
     * Testa o comportamento do endpoint createOrder quando a lista de produtos é vazia.
     * Verifica se o pedido é salvo corretamente com a lista vazia.
     */
    @Test
    public void testCreateOrder_EmptyProductList() {
        Order order = new Order();
        order.setProducts(Arrays.asList());

        when(orderService.saveOrder(any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderController.createOrder(order);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getProducts().isEmpty());

        verify(orderService).saveOrder(any(Order.class));
    }

    /**
     * Testa o comportamento do endpoint createOrder quando o pedido é nulo.
     * Verifica se uma exceção NullPointerException é lançada.
     */
    @Test
    public void testCreateOrder_NullOrder() {
        assertThrows(NullPointerException.class, () -> {
            orderController.createOrder(null);
        });
    }
}
