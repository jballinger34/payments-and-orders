package me.jamie.paymentspractice.dao.product;

import me.jamie.paymentspractice.domain.model.Product;

import java.util.List;

public interface ProductDao {

    void put(Product product);
    void remove(String productId);

    Product findById(String productId);
    List<Product> findAll();


}
