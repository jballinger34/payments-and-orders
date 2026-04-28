package dao.inventory;

import exception.PersistenceException;
import exception.ProductNotFoundException;

public interface InventoryDao {

    int getStock(String productId) throws PersistenceException, ProductNotFoundException;
    void removeStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException;
}
