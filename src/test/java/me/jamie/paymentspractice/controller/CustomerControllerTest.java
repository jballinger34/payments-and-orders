package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;


    @Test
    public void testCheckout() throws Exception {
        Order mockOrder = Order.fromPersistence("TEST_ORDER", new ArrayList<>(), mock(Payment.class), OrderStatus.CREATED);
        when(orderService.placeOrder(anyList(), any())).thenReturn(mockOrder);

        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        verify(orderService).placeOrder(anyList(), any());
        verify(orderService).authorizePayment(mockOrder);
        verify(orderService).reserveStock(mockOrder);
        verify(orderService).capturePayment(mockOrder);
    }
    @Test
    public void testCheckoutInvalidJson() throws Exception {

        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCheckoutMissingBody() throws Exception {

        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCheckoutReturnsOrderJson() throws Exception {

        Payment payment = mock(Payment.class);
        when(payment.getId()).thenReturn("PAY_123");

        Order order = Order.fromPersistence(
                "ORDER_1",
                new ArrayList<>(),
                payment,
                OrderStatus.CREATED
        );

        when(orderService.placeOrder(anyList(), any()))
                .thenReturn(order);

        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.total").value(order.getTotal()))
                .andExpect(jsonPath("$.paymentId").value(order.getPayment().getId()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()))
                .andExpect(jsonPath("$.items.length()").value(order.getItems().size()));
    }

}
