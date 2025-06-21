package com.challenge.repository;

import com.challenge.model.Order;
import com.challenge.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveAndFindOrder() {
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Product2");
        product2.setPrice(20.0);

        Order order = new Order();
        order.setOrderId("order1");
        order.setProducts(Arrays.asList(product1, product2));

        when(orderRepository.save(order)).thenReturn(order);
        when(orderRepository.findById("order1")).thenReturn(Optional.of(order));

        Order savedOrder = orderRepository.save(order);
        assertNotNull(savedOrder);

        Optional<Order> foundOrder = orderRepository.findById("order1");
        assertTrue(foundOrder.isPresent());
        assertEquals(2, foundOrder.get().getProducts().size());

        verify(orderRepository).save(order);
        verify(orderRepository).findById("order1");
    }
}
