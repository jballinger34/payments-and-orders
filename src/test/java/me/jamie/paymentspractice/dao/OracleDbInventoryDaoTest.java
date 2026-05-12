package me.jamie.paymentspractice.dao;

import me.jamie.paymentspractice.dao.inventory.OracleDbInventoryDao;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OracleDbInventoryDaoTest {

    @Autowired
    OracleDbInventoryDao dao;

    @Test
    void testSaveAndFindById() throws Exception {

        Product product = new Product(
                "TEST_PRODUCT",
                "TEST_PRODUCT",
                1.00,
                10
        );
        dao.put(product.getId(), product);

        Product found = dao.findById("TEST_PRODUCT");

        assertNotNull(found);
        assertEquals(product.getId(), found.getId());
        assertEquals(product.getName(), found.getName());
        assertEquals(product.getCost(), found.getCost());
        assertEquals(product.getStock(), found.getStock());
    }

    @Test
    void testFindAll() throws Exception {

        Product product1 = new Product(
                "TEST_PRODUCT_2",
                "TEST_PRODUCT_2",
                5.00,
                20
        );

        Product product2 = new Product(
                "TEST_PRODUCT_3",
                "TEST_PRODUCT_3",
                4.00,
                5
        );

        dao.put(product1.getId(), product1);
        dao.put(product2.getId(), product2);

        List<Product> products = dao.findAll();

        assertNotNull(products);

        assertTrue(
                products.stream().anyMatch(p -> p.getId().equals("TEST_PRODUCT_2"))
        );

        assertTrue(
                products.stream().anyMatch(p -> p.getId().equals("TEST_PRODUCT_3"))
        );
    }

    @Test
    void testGetNotFoundProduct() {
        String productId = "DOES_NOT_EXIST";
        assertThrows(ProductNotFoundException.class, () -> dao.findById(productId));
    }
    @Test
    void testRemoveNotFoundProduct() {
        String productId = "DOES_NOT_EXIST";
        assertThrows(ProductNotFoundException.class, () -> dao.remove(productId));
    }
}
