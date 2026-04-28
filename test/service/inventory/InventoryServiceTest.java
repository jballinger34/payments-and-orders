package service.inventory;

import dao.inventory.InventoryDao;
import exception.InsufficientStockException;
import exception.PersistenceException;
import exception.ProductNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InventoryService;
import service.audit.AuditService;
import service.audit.AuditServiceStub;

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
        int stock = inventoryService.getStockQty("ITEM_1");
        assertEquals(10,stock);
        stock = inventoryService.getStockQty("ITEM_2");
        assertEquals(0,stock);
        assertThrows(ProductNotFoundException.class, () -> inventoryService.getStockQty("NOT_ITEM"));
    }

    @Test
    void testIsInStock() throws PersistenceException {
        assertTrue(inventoryService.isInStock("ITEM_1",1));
        assertFalse(inventoryService.isInStock("ITEM_2",1));
        assertThrows(ProductNotFoundException.class, () -> inventoryService.isInStock("NOT_ITEM",1));
    }

    @Test
    void testReduceStock() throws PersistenceException {
        inventoryService.reduceStock("ITEM_1",5);
        assertEquals(5, inventoryService.getStockQty("ITEM_1"));

        assertThrows(InsufficientStockException.class, ()-> inventoryService.reduceStock("ITEM_1",10));
        assertThrows(IllegalArgumentException.class, () -> inventoryService.reduceStock("ITEM_1",-4) );
    }
}