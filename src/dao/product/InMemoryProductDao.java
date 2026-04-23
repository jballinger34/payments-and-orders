package dao.product;

import domain.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryProductDao implements ProductDao {

    private final Map<String, Product> products = new HashMap<>();


    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
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
