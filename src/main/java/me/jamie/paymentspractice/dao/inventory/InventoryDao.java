package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

public interface InventoryDao {

    int getStock(String productId) throws PersistenceException, ProductNotFoundException;
    void removeStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException;
}
