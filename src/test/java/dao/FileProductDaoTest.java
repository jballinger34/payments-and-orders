package dao;

import me.jamie.paymentspractice.dao.product.FileProductDao;
import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class FileProductDaoTest {

    ProductDao testDao;
    private final String testFile = "test_products.txt";

    @BeforeEach
    void setUp() throws IOException, PersistenceException {
        //blank out file
        new FileWriter(testFile);
        testDao = new FileProductDao(testFile);
    }

    @Test
    void testSaveToFile(){
        String productId = "TEST_PRODUCT_1";
        Product product = new Product(productId, productId, 1);

        try(Scanner scanner = new Scanner(new FileReader(testFile))){
            testDao.put(product);
            String entry = scanner.nextLine();

            String[] tokens = entry.split("::");
            assertEquals(productId, tokens[0]);
            assertEquals(productId, tokens[1]);
            assertEquals(1.0, Double.parseDouble(tokens[2]));
        } catch (IOException | PersistenceException e){
            fail("Test setup failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testLoadFromFile(){
        String productId = "TEST_PRODUCT_1";
        int cost = 10;
        String testEntry = productId + "::" + productId + "::" + cost;
        try (PrintWriter out = new PrintWriter(new FileWriter(testFile))){
            out.println(testEntry);
            out.flush();
            //CONSTRUCTOR CALLS THE LOAD METHOD THAT WE ARE TESTING
            testDao = new FileProductDao(testFile);
            Product product = testDao.findById(productId);
            //normally name wouldnt be id but here we just set name/id to be the same
            assertEquals(productId, product.getName());
            assertEquals(cost, product.getCost());

        } catch (IOException | PersistenceException e){
            fail("Test setup failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testAddFindProduct(){
        String productId = "TEST_PRODUCT_1";
        Product product = new Product(productId, "A Product", 10);
        try {
            testDao.put(product);

            assertEquals(productId, testDao.findById(productId).getId());
            assertEquals(product.getName(), testDao.findById(productId).getName());
            assertEquals(product.getCost(), testDao.findById(productId).getCost());
        } catch (PersistenceException e){
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }

    }
    @Test
    void testUpdateFindProduct(){
        String productId = "TEST_PRODUCT_1";
        try{
            testDao.put(new Product(productId, "A Product", 10));
            Product product1 = new Product(productId, "Renamed product", 100);
            testDao.put(product1);

            assertEquals(productId, testDao.findById(productId).getId());
            assertEquals(product1.getName(), testDao.findById(productId).getName());
            assertEquals(product1.getCost(), testDao.findById(productId).getCost());
        } catch (PersistenceException e){
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }
    }

    @Test
    void testRemoveProduct(){
        String productId = "TEST_PRODUCT_1";
        try{
            testDao.put(new Product(productId,productId,10));
            testDao.remove(productId);
            assertThrows(ProductNotFoundException.class, () -> testDao.findById(productId));

        } catch (PersistenceException e){
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }
    }
    @Test
    void testRemoveProductNotFound(){
        String productId = "NOT_A_REAL_PRODUCT";
        assertThrows(ProductNotFoundException.class, () -> testDao.remove(productId));
    }

    @Test
    void testFindProductNotFound() {
        try {
            testDao.put(new Product("TEST_PRODUCT_1", "A Product", 10));
            assertThrows(ProductNotFoundException.class, () -> testDao.findById("TEST_PRODUCT_2"));
        } catch (PersistenceException e) {
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }
    }
    @Test
    void testFindAllEmpty(){
        try{
            List<Product> productList = testDao.findAll();

            assertNotNull(productList);
            assertEquals(0, productList.size());
        } catch (PersistenceException e) {
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }
    }

    @Test
    void testFindAll(){
        try {
            String duplicateId = "DUPLICATE_PRODUCT";
            testDao.put(new Product(duplicateId, duplicateId, 5));
            //duplicate below, will update
            testDao.put(new Product(duplicateId, "new_name", 10));
            testDao.put(new Product("TEST_PRODUCT_1", "name", 10));
            testDao.put(new Product("TEST_PRODUCT_2", "name", 10));

            assertEquals(3, testDao.findAll().size());
        } catch (PersistenceException e){
            fail("Test setup failed due to persistance exception: " + e.getMessage());
        }
    }



}
