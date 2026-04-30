package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.List;

public interface InventoryDao {

    Product findById(String productId) throws PersistenceException, ProductNotFoundException;

    List<Product> findAll() throws PersistenceException;

    void put(String productId, Product product) throws PersistenceException;

    void remove(String productId) throws PersistenceException, ProductNotFoundException;
}
