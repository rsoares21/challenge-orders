package com.challenge.messaging;

import com.challenge.model.Order;
import com.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class OrderMessageListenerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderMessageListener orderMessageListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveOrderMessage_Success() throws Exception {
        String message = "{\"orderId\":\"123\"}";
        Order order = new Order();
        order.setOrderId("123");

        when(objectMapper.readValue(message, Order.class)).thenReturn(order);

        orderMessageListener.receiveOrderMessage(message);

        verify(objectMapper, times(1)).readValue(message, Order.class);
        verify(orderService, times(1)).saveOrder(order);
    }

    @Test
    void testReceiveOrderMessage_Exception() throws Exception {
        String message = "invalid json";

        when(objectMapper.readValue(message, Order.class)).thenThrow(new RuntimeException("JSON parse error"));

        orderMessageListener.receiveOrderMessage(message);

        verify(objectMapper, times(1)).readValue(message, Order.class);
        verify(orderService, never()).saveOrder(any());
    }

    @Test
    void testReceiveOrderMessage_NullMessage() throws Exception {
        String message = null;

        when(objectMapper.readValue(message, Order.class)).thenThrow(new IllegalArgumentException("Message is null"));

        orderMessageListener.receiveOrderMessage(message);

        verify(objectMapper, times(1)).readValue(message, Order.class);
        verify(orderService, never()).saveOrder(any());
    }

    @Test
    void testReceiveOrderMessage_EmptyMessage() throws Exception {
        String message = "";

        when(objectMapper.readValue(message, Order.class)).thenThrow(new IllegalArgumentException("Message is empty"));

        orderMessageListener.receiveOrderMessage(message);

        verify(objectMapper, times(1)).readValue(message, Order.class);
        verify(orderService, never()).saveOrder(any());
    }
}
