package com.challenge.controller;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order order;

    @BeforeEach
    public void setup() {
        Product product1 = new Product();
        product1.setName("Product1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Product2");
        product2.setPrice(20.0);

        order = new Order();
        order.setOrderId("order1");
        order.setProducts(Arrays.asList(product1, product2));    
    }

    @Test
    public void testCreateOrder() throws Exception {
        when(orderService.saveOrder(any(Order.class))).thenReturn(order);

        String orderJson = "{\"orderId\":\"order1\",\"products\":[{\"name\":\"Product1\",\"price\":10.0},{\"name\":\"Product2\",\"price\":20.0}]}";

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order1"))
                .andExpect(jsonPath("$.products", hasSize(2)));
    }

    @Test
    public void testGetAllOrders() throws Exception {
        order.setOrderStatus(com.challenge.model.OrderStatus.PROCESSING);
        List<Order> orders = Arrays.asList(order);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId").value("order1"));
    }
}
