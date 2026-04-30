package me.jamie.paymentspractice.service;

import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;

public class ProductService {

    ProductDao productDao;

    public ProductService(ProductDao productDao){
        this.productDao = productDao;
    }

    public Product getProduct(String productId) throws PersistenceException {
        return productDao.findById(productId);
    }
    public List<Product> getAllProducts() throws PersistenceException {
        return productDao.findAll();
    }


}
