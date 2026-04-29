package dao;

import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.product.FileProductDao;
import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

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
    void testLoadFromFile(){
        fail("TEST NOT WRITTEN");
    }
    @Test
    void testSaveToFile(){
        fail("TEST NOT WRITTEN");
    }
    @Test
    void testAddNewProduct(){
        fail("TEST NOT WRITTEN");
    }
    @Test
    void testUpdateProduct(){
        fail("TEST NOT WRITTEN");
    }
    @Test
    void testFindById(){
        fail("TEST NOT WRITTEN");
    }
    @Test
    void testFindAll(){
        fail("TEST NOT WRITTEN");
    }



}
