package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

public class FileInventoryDao implements InventoryDao {

    String file;

    public FileInventoryDao(String file){
        this.file = file;
    }

    @Override
    public int getStock(String productId) throws PersistenceException, ProductNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void alterStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    @Override
    public void addProduct(String productId) throws PersistenceException, DuplicateProductException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
