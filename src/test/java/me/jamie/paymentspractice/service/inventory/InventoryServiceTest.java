package me.jamie.paymentspractice.service.inventory;


import me.jamie.paymentspractice.stubs.InventoryDaoStubImpl;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.audit.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import me.jamie.paymentspractice.stubs.AuditServiceStub;


import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {
    InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        InventoryDao inventoryDao = new InventoryDaoStubImpl();
        AuditService auditService = new AuditServiceStub();
        inventoryService = new InventoryService(inventoryDao, auditService);
    }

    @Test
    void testGetStockQty() throws PersistenceException {
        int stock = inventoryService.getStockQty("PRODUCT_1");
        assertEquals(10,stock);
        stock = inventoryService.getStockQty("PRODUCT_2");
        assertEquals(0,stock);
        assertThrows(ProductNotFoundException.class, () -> inventoryService.getStockQty("NOT_ITEM"));
    }

    @Test
    void testIsInStock() throws PersistenceException {
        assertTrue(inventoryService.isInStock("PRODUCT_1",1));
        assertFalse(inventoryService.isInStock("PRODUCT_2",1));
        assertThrows(ProductNotFoundException.class, () -> inventoryService.isInStock("NOT_ITEM",1));
    }

    @Test
    void testReduceStock() throws PersistenceException {
        inventoryService.reduceStock("PRODUCT_1",5);
        assertEquals(5, inventoryService.getStockQty("PRODUCT_1"));

        assertThrows(InsufficientStockException.class, ()-> inventoryService.reduceStock("PRODUCT_1",10));
        assertThrows(IllegalArgumentException.class, () -> inventoryService.reduceStock("PRODUCT_1",-4) );
    }
}