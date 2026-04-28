package me.jamie.paymentspractice.service;

import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.domain.model.Product;

import java.util.List;

public class ProductService {

    ProductDao productDao;

    public ProductService(ProductDao productDao){
        this.productDao = productDao;
    }

    public Product getProduct(String productId){
        return productDao.findById(productId);
    }
    public List<Product> getAllProducts(){
        return productDao.findAll();
    }


}
