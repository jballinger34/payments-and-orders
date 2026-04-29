package dao;

import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.product.FileProductDao;
import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

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

    }
    @Test
    void testSaveToFile(){

    }
    @Test
    void testAddNewProduct(){

    }
    @Test
    void testUpdateProduct(){

    }
    @Test
    void testFindById(){

    }
    @Test
    void testFindAll(){

    }



}
