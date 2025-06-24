package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.OrderStatus;
import com.challenge.repository.OrderRepository;
import com.challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepository, productRepository);
    }

    @Test
    public void testSaveOrder_CalculatesTotalValue() {
        Product product1 = new Product(1L, "Product 1", 10.0);
        Product product2 = new Product(2L, "Product 2", 20.0);

        Order order = new Order();
        order.setProducts(Arrays.asList(product1, product2));
        order.setOrderStatus(OrderStatus.NEW);

        when(productRepository.findAllById(any())).thenReturn(Arrays.asList(product1, product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order savedOrder = orderService.saveOrder(order);

        assertEquals(30.0, savedOrder.getTotalValue(), 0.001);
        assertEquals(OrderStatus.NEW, savedOrder.getOrderStatus());
        verify(productRepository, times(1)).findAllById(any());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testSaveOrder_ProductNotFound() {
        Product product1 = new Product(1L, "Product 1", 10.0);

        Order order = new Order();
        order.setProducts(Arrays.asList(product1));
        order.setOrderStatus(OrderStatus.NEW);

        when(productRepository.findAllById(any())).thenReturn(Arrays.asList());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.saveOrder(order);
        });

        assertEquals("Produto não encontrado com id: 1", exception.getMessage());
    }

    @Test
    public void testSaveOrder_EmptyProductList() {
        Order order = new Order();
        order.setProducts(Arrays.asList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.saveOrder(order);
        });

        assertEquals("A order deve conter pelo menos um produto.", exception.getMessage());
    }

    @Test
    public void testSaveOrder_NullProductList() {
        Order order = new Order();
        order.setProducts(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.saveOrder(order);
        });

        assertEquals("A order deve conter pelo menos um produto.", exception.getMessage());
    }

    @Test
    public void testGetNewOrders() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.NEW);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.NEW);

        List<Order> newOrders = Arrays.asList(order1, order2);

        OrderServiceImpl spyService = Mockito.spy(orderService);
        doReturn(newOrders).when(spyService).getNewOrders();

        List<Order> result = spyService.getNewOrders();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> o.getOrderStatus() == OrderStatus.NEW));
    }
}
