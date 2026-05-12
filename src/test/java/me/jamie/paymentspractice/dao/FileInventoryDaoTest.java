package me.jamie.paymentspractice.dao;

import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.spec.DESedeKeySpec;
import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FileInventoryDaoTest {

    private FileInventoryDao testDao;
    private String testFile = "test_inventory.txt";

    @BeforeEach
    void setUp() throws IOException, PersistenceException {
        //blank out file
        new FileWriter(testFile);
        testDao = new FileInventoryDao(testFile);
    }

    @Test
    void testSaveToFile() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.put(productId,new Product(productId,productId,1,10));

        try(Scanner scanner = new Scanner(new FileReader(testFile))){
            String entry = scanner.nextLine();

            String[] tokens = entry.split("::");
            assertEquals(productId, tokens[0]);
            assertEquals(productId, tokens[1]);
            assertEquals(1, Double.parseDouble(tokens[2]));
            assertEquals(10, Integer.parseInt(tokens[3]));

        } catch (IOException e){
            fail("Test setup failed due to IOException: " + e.getMessage());
        }
    }
    @Test
    void testLoadFile() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        double cost = 1;
        int quantity = 10;
        String testEntry = productId + "::" + productId + "::" + cost + "::" + quantity;
        try (PrintWriter out = new PrintWriter(new FileWriter(testFile))){
            out.println(testEntry);
            out.flush();
            //CONSTRUCTOR CALLS THE LOAD METHOD THAT WE ARE TESTING
            testDao = new FileInventoryDao(testFile);
            testDao.init();
            int quantityFound = testDao.findById(productId).getStock();
            assertEquals(quantity, quantityFound);

        } catch (IOException e){
            fail("Test setup failed due to IOException: " + e.getMessage());
        }
    }

    @Test
    void testAddGetStock() throws PersistenceException {
        String productId = "TEST_PRODUCT_1";
        testDao.put(productId,new Product(productId,productId,1,10));
        assertEquals(10, testDao.findById(productId).getStock());
    }
    @Test
    void testGetNotFoundProduct() {
        String productId = "TEST_PRODUCT_1";
        assertThrows(ProductNotFoundException.class, () -> testDao.findById(productId));
    }
    @Test
    void testRemoveNotFoundProduct() {
        String productId = "TEST_PRODUCT_1";
        assertThrows(ProductNotFoundException.class, () -> testDao.remove(productId));
    }

}