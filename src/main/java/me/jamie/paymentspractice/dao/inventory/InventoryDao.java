package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.List;

public interface InventoryDao {

    ProductEntity findByMerchantIdAndId(String merchantId, String productId) throws PersistenceException, ProductNotFoundException;

    List<ProductEntity> findByMerchantId(String merchantId) throws PersistenceException;

    void save(ProductEntity productEntity) throws PersistenceException;

    void remove(String merchantId, String productId) throws PersistenceException, ProductNotFoundException;

    int getStock(String merchantId, String productId) throws PersistenceException, ProductNotFoundException;
}
