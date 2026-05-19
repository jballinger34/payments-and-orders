package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InventoryService service;


    //TODO
    // add test for the contract of getALlProducts
    // and verify methods called?


    @Test
    void testGetProductReturnsProductDto() throws Exception {
        Product product = new Product(
                "PROD_1",
                "TEST_MERCHANT",
                "Keyboard",
                49.99,
                10
        );

        when(service.getProduct("TEST_MERCHANT","PROD_1")).thenReturn(product);

        mockMvc.perform(get("/TEST_MERCHANT/inventory/PROD_1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getCost()))
                .andExpect(jsonPath("$.stock").value(product.getStock()));
    }

    @Test
    void testCreateProductReturnsCreatedProduct() throws Exception {

        Product product = new Product(
                "PROD_2",
                "TEST_MERCHANT",
                "Mouse",
                19.99,
                5
        );

        when(service.createProduct(any(),any(), anyDouble(), anyInt())).thenReturn(product);

        mockMvc.perform(post("/TEST_MERCHANT/inventory")
                        .param("name", "Mouse")
                        .param("cost", "19.99")
                        .param("stock", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getCost()))
                .andExpect(jsonPath("$.stock").value(product.getStock()));
    }

    @Test
    void testRestockProductReturnsUpdatedProduct() throws Exception {
        Product updated = new Product(
                "PROD_1",
                "TEST_MERCHANT",
                "Keyboard",
                49.99,
                20
        );

        when(service.restockProduct("TEST_MERCHANT","PROD_1", 10)).thenReturn(updated);

        mockMvc.perform(post("/TEST_MERCHANT/inventory/PROD_1/restock")
                        .param("amount", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId()))
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.price").value(updated.getCost()))
                .andExpect(jsonPath("$.stock").value(updated.getStock()));
    }
    @Test
    void testSetPriceReturnsUpdatedProduct() throws Exception {

        Product updated = new Product(
                "PROD_1",
                "TEST_MERCHANT",
                "Keyboard",
                99.99,
                10
        );

        when(service.setPrice("TEST_MERCHANT","PROD_1", 99.99))
                .thenReturn(updated);

        mockMvc.perform(put("/TEST_MERCHANT/inventory/PROD_1/price")
                        .param("price", "99.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId()))
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.price").value(updated.getCost()))
                .andExpect(jsonPath("$.stock").value(updated.getStock()));
    }

    @Test
    void testSetStockReturnsUpdatedProduct() throws Exception {

        Product updated = new Product(
                "PROD_1",
                "TEST_MERCHANT",
                "Keyboard",
                49.99,
                50
        );

        when(service.setStock("TEST_MERCHANT","PROD_1", 50)).thenReturn(updated);

        mockMvc.perform(put("/TEST_MERCHANT/inventory/PROD_1/stock")
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId()))
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.price").value(updated.getCost()))
                .andExpect(jsonPath("$.stock").value(updated.getStock()));
    }

    @Test
    void testSetNameReturnsUpdatedProduct() throws Exception {

        Product updated = new Product(
                "PROD_1",
                "TEST_MERCHANT",
                "Mechanical Keyboard",
                49.99,
                10
        );

        when(service.setName("TEST_MERCHANT","PROD_1", "Mechanical Keyboard")).thenReturn(updated);

        mockMvc.perform(put("/TEST_MERCHANT/inventory/PROD_1/name")
                        .param("name", "Mechanical Keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updated.getName()));
    }
    @Test
    void testDeleteProductReturnsEmptyBody() throws Exception {
        doNothing().when(service).deleteProduct("TEST_MERCHANT","PROD_1");

        mockMvc.perform(delete("/TEST_MERCHANT/inventory/PROD_1")
                        .param("name", "Mechanical Keyboard"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void testInvalidJsonReturns400() throws Exception {

        mockMvc.perform(post("/TEST_MERCHANT/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testMissingParamReturns400() throws Exception {

        mockMvc.perform(post("/TEST_MERCHANT/inventory/PROD_1/restock"))
                .andExpect(status().isBadRequest());
    }


}
