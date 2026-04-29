package dao;



import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class InventoryDaoStubImpl implements InventoryDao {
    private final Map<String, Integer> inventory = new HashMap<>();

    public InventoryDaoStubImpl(){
        inventory.put("ITEM_1",10);
        inventory.put("ITEM_2",0);
    }

    @Override
    public int getStock(String productId) throws PersistenceException, ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product not found");
        }
        return inventory.get(productId);
    }

    @Override
    public void alterStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        if (!inventory.containsKey(productId)) {
            throw new ProductNotFoundException("Product not found");
        }
        int currentStock = inventory.get(productId);
        inventory.put(productId, currentStock + quantity);
    }

    @Override
    public void addProduct(String productId) throws PersistenceException, DuplicateProductException {
        if(inventory.containsKey(productId)){
            throw new DuplicateProductException("Product " + productId + " already in inventory");
        }
        inventory.put(productId,0);
    }
}
