package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

public interface InventoryDao {
    /**
     * gets the quantity of a product in the inventory
     *
     * @param productId the product to look up
     * @return the quantity of that product
     * @throws PersistenceException if any problems with accessing the storage occur
     * @throws ProductNotFoundException if the product is not in the inventory
     */
    int getStock(String productId) throws PersistenceException, ProductNotFoundException;

    /**
     * used to change the stock amount of a product in inventory
     *
     * @param productId the product to alter stock amount for
     * @param quantity the amount to change the stock by
     * @throws PersistenceException if any problems with accessing the storage occur
     * @throws ProductNotFoundException
     */
    void alterStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException;

    /**
     * Used to add a product to the inventory
     * this product will then need to be stocked with alterStock
     * so by default the product added will have 0 stock
     *
     * @param productId the id of the product you want to add to inventory
     * @throws PersistenceException if any problems with accessing the storage occur
     * @throws DuplicateProductException if the product is already in the inventory
     */
    void addProduct(String productId) throws PersistenceException, DuplicateProductException;
}
