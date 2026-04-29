package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

public class AlwaysInStockInventoryDao implements InventoryDao {

    private final int MAX_ORDER_SIZE = 100;

    // a dummy impl that always has 100 stock
    @Override
    public int getStock(String productId) throws PersistenceException, ProductNotFoundException {
        return MAX_ORDER_SIZE;
    }

    @Override
    public void alterStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        return;
    }

}
