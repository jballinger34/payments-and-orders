package stubs;

import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryDaoStubImpl implements InventoryDao {
    private final Map<String, Product> inventory = new HashMap<>();

    public InventoryDaoStubImpl(){
        inventory.put("PRODUCT_1", new Product("PRODUCT_1","PRODUCT_1", 5.0,10));
        inventory.put("PRODUCT_2", new Product("PRODUCT_2","PRODUCT_2", 100.0,0));
    }

    @Override
    public Product findById(String productId) throws PersistenceException, ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product not found");
        }
        return inventory.get(productId);
    }

    @Override
    public List<Product> findAll() throws PersistenceException {
        return new ArrayList<>(inventory.values());
    }

    @Override
    public void put(String productId, Product product) throws PersistenceException {
        inventory.put(productId,product);
    }

    @Override
    public void remove(String productId) throws PersistenceException, ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product not found");
        }
        inventory.remove(productId);
    }
}
