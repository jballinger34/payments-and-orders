package me.jamie.paymentspractice.dao.product;

import me.jamie.paymentspractice.domain.model.Product;

import java.util.List;

public class FileProductDao implements ProductDao {

    private final String PRODUCTS_FILE;

    public FileProductDao(String products_file){
        this.PRODUCTS_FILE = products_file;
    }

    private void loadProducts(){

    }
    private void writeAllProducts(){

    }

    @Override
    public void put(Product product) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void remove(String productId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Product findById(String productId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<Product> findAll() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
