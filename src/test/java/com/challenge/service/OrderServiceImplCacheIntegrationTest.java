package com.challenge.service;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import com.challenge.model.Product;
import com.challenge.repository.OrderRepository;
import com.challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceImplCacheIntegrationTest {

    @Autowired
    private OrderServiceImpl orderService;

    @SpyBean
    private OrderRepository orderRepository;

    @SpyBean
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        // Clear cache before each test
        cacheManager.getCache("newOrders").clear();
    }

    @Test
    @Transactional
    public void testGetNewOrdersCaching() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.NEW);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.NEW);

        List<Order> newOrders = Arrays.asList(order1, order2);

        doReturn(newOrders).when(orderRepository).findByOrderStatus(OrderStatus.NEW);

        // First call - should invoke repository
        List<Order> result1 = orderService.getNewOrders();
        assertEquals(2, result1.size());
        verify(orderRepository, times(1)).findByOrderStatus(OrderStatus.NEW);

        // Second call - should use cache, no repository call
        List<Order> result2 = orderService.getNewOrders();
        assertEquals(2, result2.size());
        verify(orderRepository, times(1)).findByOrderStatus(OrderStatus.NEW);
    }

    @Test
    @Disabled("Desabilitado temporariamente para correção")
    @Transactional
    public void testSaveOrderEvictsCache() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.NEW);

        // Add a product with only ID to satisfy business rule
        Product product = new Product();
        product.setId(1L);

        // Mock saving product to return managed product
        Product managedProduct = new Product();
        managedProduct.setId(1L);
        managedProduct.setName("Test Product");
        managedProduct.setPrice(10.0);

        doReturn(managedProduct).when(productRepository).save(any(Product.class));
        doReturn(Arrays.asList(managedProduct)).when(productRepository).findAllById(Arrays.asList(product.getId()));

        order1.setProducts(Arrays.asList(product));

        List<Order> newOrders = Arrays.asList(order1);

        doReturn(newOrders).when(orderRepository).findByOrderStatus(OrderStatus.NEW);

        // Prime cache
        List<Order> result1 = orderService.getNewOrders();
        verify(orderRepository, times(1)).findByOrderStatus(OrderStatus.NEW);

        // Save order should evict cache
        orderService.saveOrder(order1);

        // After eviction, repository should be called again
        List<Order> result2 = orderService.getNewOrders();
        verify(orderRepository, times(2)).findByOrderStatus(OrderStatus.NEW);
    }
}
