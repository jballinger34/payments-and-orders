package me.jamie.paymentspractice.service.inventory;


import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.audit.AuditService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    InventoryDao inventoryDao;
    @Mock
    AuditService auditService;

    @InjectMocks
    InventoryService inventoryService;


    @Test
    void testGetStockQty() throws Exception {
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_1")).thenReturn(10);
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_2")).thenReturn(0);
        when(inventoryDao.getStock("TEST_MERCHANT","NOT_PRODUCT_3")).thenThrow(new ProductNotFoundException("NOT_PRODUCT_3"));


        int stock = inventoryService.getStockQty("TEST_MERCHANT","PRODUCT_1");
        assertEquals(10,stock);

        stock = inventoryService.getStockQty("TEST_MERCHANT","PRODUCT_2");
        assertEquals(0,stock);

        assertThrows(ProductNotFoundException.class, () -> inventoryService.getStockQty("TEST_MERCHANT","NOT_PRODUCT_3"));
    }

    @Test
    void testIsInStock() throws PersistenceException {
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_1")).thenReturn(10);
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_2")).thenReturn(0);
        when(inventoryDao.getStock("TEST_MERCHANT","NOT_PRODUCT_3")).thenThrow(new ProductNotFoundException("NOT_PRODUCT_3"));

        assertTrue(inventoryService.isInStock("TEST_MERCHANT","PRODUCT_1",1));
        assertFalse(inventoryService.isInStock("TEST_MERCHANT","PRODUCT_2",1));
        assertThrows(ProductNotFoundException.class, () -> inventoryService.isInStock("TEST_MERCHANT","NOT_PRODUCT_3",1));
    }

    @Test
    void testReduceStockSuccessfully() throws Exception {
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_1")).thenReturn(10);

        ProductEntity entity = new ProductEntity("PRODUCT_1","TEST_MERCHANT","PRODUCT_1",1.0,10);
        when(inventoryDao.findByMerchantIdAndId("TEST_MERCHANT","PRODUCT_1")).thenReturn(entity);

        Product product1 = inventoryService.reduceStock("TEST_MERCHANT","PRODUCT_1",3);

        assertEquals(7, product1.getStock());
        verify(inventoryDao).save(any(ProductEntity.class));
        verify(auditService).logSuccess(any(), any(), any());
    }
    @Test
    void testReduceStockTo0() throws Exception {
        when(inventoryDao.getStock("TEST_MERCHANT", "PRODUCT_1")).thenReturn(10);

        ProductEntity entity = new ProductEntity("PRODUCT_1","TEST_MERCHANT","PRODUCT_1",1.0,10);
        when(inventoryDao.findByMerchantIdAndId("TEST_MERCHANT","PRODUCT_1")).thenReturn(entity);

        Product product1 = inventoryService.reduceStock("TEST_MERCHANT","PRODUCT_1",10);

        assertEquals(0, product1.getStock());
    }

    @Test
    void testReduceInsufficientStock() throws Exception {
        ProductEntity entity = new ProductEntity("PRODUCT_1","TEST_MERCHANT","PRODUCT_1",1.0,10);
        when(inventoryDao.findByMerchantIdAndId("TEST_MERCHANT", "PRODUCT_1")).thenReturn(entity);

        assertThrows(InsufficientStockException.class, ()-> inventoryService.reduceStock("TEST_MERCHANT","PRODUCT_1",100));
    }

    @Test
    void testReduceNegativeStock(){
        assertThrows(IllegalArgumentException.class, () -> inventoryService.reduceStock("TEST_MERCHANT","PRODUCT_1",-4) );
    }
}