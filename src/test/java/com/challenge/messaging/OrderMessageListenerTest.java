package com.challenge.messaging;

import com.challenge.model.Order;
import com.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderMessageListenerTest {

    private OrderService orderService;
    private ObjectMapper objectMapper;
    private OrderMessageListener listener;

    @BeforeEach
    public void setup() {
        orderService = mock(OrderService.class);
        objectMapper = new ObjectMapper();
        listener = new OrderMessageListener(orderService, objectMapper);
    }

    @Test
    public void testReceiveOrderMessage_validMessage_callsAddOrder() throws Exception {
        String json = "{\"orderId\":\"order123\",\"products\":[]}";

        listener.receiveOrderMessage(json);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService, times(1)).addOrder(orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();
        assertEquals("order123", capturedOrder.getOrderId());
    }

    @Test
    public void testReceiveOrderMessage_invalidMessage_logsError() {
        String invalidJson = "invalid json";

        listener.receiveOrderMessage(invalidJson);

        // No exception thrown, error logged (manual verification or extend with logging framework)
        verify(orderService, never()).addOrder(any());
    }
}
