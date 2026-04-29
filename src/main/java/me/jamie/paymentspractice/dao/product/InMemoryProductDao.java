package me.jamie.paymentspractice.dao.product;

import me.jamie.paymentspractice.domain.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryProductDao implements ProductDao {

    private final Map<String, Product> products = new HashMap<>();

    public InMemoryProductDao(){
        Product apple = new Product("0","Apple", 0.50);
        Product orange = new Product("1","Orange", 0.75);
        products.put(apple.getId(),apple);
        products.put(orange.getId(), orange);
    }

    @Override
    public void put(Product product) {
        products.put(product.getId(), product);
    }
    @Override
    public void remove(String productId){
        products.remove(productId);
    }

    @Override
    public Product findById(String productId) {
        return products.get(productId);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
