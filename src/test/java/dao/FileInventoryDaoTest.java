package dao;

import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileInventoryDaoTest {

    private InventoryDao testDao;

    @BeforeEach
    void setUp() throws IOException {
        String testFile = "test_inventory.txt";
        //blank out file
        new FileWriter(testFile);
        testDao = new FileInventoryDao(testFile);
    }

    @Test
    void testAddGetDefaultStock() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.addProduct(productId);

        // ensure any new products added have 0 stock by default
        assertEquals(0, testDao.getStock(productId));
    }
    @Test
    void testAddAlterGetStock() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.addProduct(productId);
        testDao.alterStock(productId,10);
        assertEquals(0, testDao.getStock(productId));
    }
    @Test
    void testAddDuplicateProduct() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.addProduct(productId);
        assertThrows(DuplicateProductException.class, () -> testDao.addProduct(productId));
    }
    @Test
    void testGetNotFoundProduct() {
        String productId = "TEST_PRODUCT_1";
        assertThrows(ProductNotFoundException.class, () -> testDao.getStock(productId));
    }
    @Test
    void testAlterNotFoundProduct() {
        String productId = "TEST_PRODUCT_1";
        assertThrows(ProductNotFoundException.class, () -> testDao.alterStock(productId, 10));
    }

}