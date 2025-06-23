package com.challenge.controller;

import com.challenge.model.Order;
import com.challenge.model.OrderStatus;
import com.challenge.model.Product;
import com.challenge.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setOrderId("order123");
        Product product1 = new Product();
        product1.setPrice(10.0);
        Product product2 = new Product();
        product2.setPrice(20.0);
        order.setProducts(Arrays.asList(product1, product2));

        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order123")))
                .andExpect(jsonPath("$.totalValue", is(30.0)))
                .andExpect(jsonPath("$.orderStatus", is(OrderStatus.NEW.toString())));
    }

    @Test
    void testGetNewOrders() throws Exception {
        Order order1 = new Order();
        order1.setOrderId("order1");
        order1.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setOrderId("order2");
        order2.setOrderStatus(OrderStatus.NEW);
        orderRepository.save(order2);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].orderId", is("order1")))
                .andExpect(jsonPath("$[1].orderId", is("order2")));
    }
}
