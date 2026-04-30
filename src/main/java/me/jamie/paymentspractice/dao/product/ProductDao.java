package me.jamie.paymentspractice.dao.product;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.List;

public interface ProductDao {

    void put(Product product) throws PersistenceException;
    void remove(String productId) throws PersistenceException, ProductNotFoundException;

    Product findById(String productId) throws PersistenceException, ProductNotFoundException;
    List<Product> findAll() throws PersistenceException;


}
