package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.GlobalExceptionHandler;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;
    @MockitoBean
    private OrderService orderService;

    @Test
    public void testPersistenceException() throws Exception {
        when(inventoryService.getAllProducts()).thenThrow(new PersistenceException("Simulating issue with DB"));
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testNoStockException() throws Exception {
        when(orderService.placeOrder(anyList(), any())).thenThrow(new InsufficientStockException("Simulating no stock"));
        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isConflict());
    }
    @Test
    void testProductNotFoundException() throws Exception {
        when(inventoryService.getProduct(any())).thenThrow(new ProductNotFoundException("Simulating product not found"));
        mockMvc.perform(get("/inventory/NOT_FOUND_PRODUCT"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testIllegalArgumentException() throws Exception {
        when(inventoryService.restockProduct(any(), anyInt())).thenThrow(new IllegalArgumentException("Simulating illegal argument given"));
        mockMvc.perform(post("/inventory/PROD_1/restock").param("amount", "-10"))
                .andExpect(status().isBadRequest());

    }
    @Test
    public void testIllegalStateException() throws Exception {

        doThrow(new IllegalStateException("Simulating auth failure")).when(orderService).reserveStock(any());

        mockMvc.perform(post("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isConflict());
    }


}
