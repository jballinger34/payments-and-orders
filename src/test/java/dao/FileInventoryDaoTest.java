package dao;

import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FileInventoryDaoTest {

    private InventoryDao testDao;
    private final String testFile = "test_inventory.txt";

    @BeforeEach
    void setUp() throws IOException, PersistenceException {
        //blank out file
        new FileWriter(testFile);
        testDao = new FileInventoryDao(testFile);
    }

    @Test
    void testSaveToFile() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.addProduct(productId);
        testDao.alterStock(productId,10);

        try(Scanner scanner = new Scanner(new FileReader(testFile))){
            String entry = scanner.nextLine();

            String[] productIdAndQuantity = entry.split("::");
            assertEquals(productId, productIdAndQuantity[0]);
            assertEquals("10", productIdAndQuantity[1]);
        } catch (IOException e){
            fail("Test setup failed due to IOException: " + e.getMessage());
        }
    }
    @Test
    void testLoadFile() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        int quantity = 10;
        String testEntry = productId + "::" + quantity;
        try (PrintWriter pr = new PrintWriter(new FileWriter(testFile))){
            pr.println(testEntry);

            //CONSTRUCTOR CALLS THE LOAD METHOD THAT WE ARE TESTING
            testDao = new FileInventoryDao(testFile);
            int quantityFound = testDao.getStock(productId);
            assertEquals(quantity, quantityFound);

        } catch (IOException e){
            fail("Test setup failed due to IOException: " + e.getMessage());
        }
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
        assertEquals(10, testDao.getStock(productId));
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