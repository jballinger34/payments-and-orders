package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.repository.OracleDbInventoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class OracleDbInventoryDao implements InventoryDao {

    OracleDbInventoryRepository repo;

    public OracleDbInventoryDao(OracleDbInventoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductEntity findByMerchantIdAndId(String merchantId, String productId) throws ProductNotFoundException {
        return repo.findByMerchantIdAndId(merchantId,productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public List<ProductEntity> findByMerchantId(String merchantId) {
        return repo.findByMerchantId(merchantId);
    }

    @Override
    public void save(ProductEntity product) throws PersistenceException {
        repo.save(product);
    }

    @Override
    public void remove(String merchantId, String productId) throws ProductNotFoundException {
        repo.deleteByMerchantIdAndId(merchantId,productId);
    }

    @Override
    public int getStock(String merchantId, String productId) throws PersistenceException, ProductNotFoundException {
        return this.findByMerchantIdAndId(merchantId,productId).getStock();
    }
}
