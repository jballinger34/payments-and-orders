package dao.product;

import domain.model.Product;

import java.util.List;

public interface ProductDao {

    void save(Product product);
    Product findById(String productId);
    List<Product> findAll();


}
