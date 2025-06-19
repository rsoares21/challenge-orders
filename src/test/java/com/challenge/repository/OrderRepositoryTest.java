package com.challenge.repository;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveAndFindOrder() {
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Product2");
        product2.setPrice(20.0);

        Order order = new Order("order1", Arrays.asList(product1, product2));

        Order savedOrder = orderRepository.save(order);
        assertNotNull(savedOrder);

        Optional<Order> foundOrder = orderRepository.findById("order1");
        assertTrue(foundOrder.isPresent());
        assertEquals(2, foundOrder.get().getProducts().size());
    }
}
