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
    public Product findById(String productId) throws PersistenceException, ProductNotFoundException {
        try {
            Optional<ProductEntity> entity = repo.findById(productId);
            if (entity.isEmpty()) throw new ProductNotFoundException(productId);
            return Product.fromPersistence(entity.get());
        } catch (DataAccessException e){
            throw new PersistenceException("Failed to retrieve product",e);
        }
    }

    @Override
    public List<Product> findAll() throws PersistenceException {
        try{
            return repo.findAll().stream().map(Product::fromPersistence).toList();
        } catch (DataAccessException e){
            throw new PersistenceException("Failed to retrieve products",e);
        }
    }

    @Override
    public void put(String productId, Product product) throws PersistenceException {
        try{
            repo.save(ProductEntity.from(product));
        } catch (DataAccessException e){
            throw new PersistenceException("Failed to save product",e);
        }
    }

    @Override
    public void remove(String productId) throws PersistenceException, ProductNotFoundException {
        try {
            Optional<ProductEntity> entity = repo.findById(productId);
            if(entity.isEmpty()) throw new ProductNotFoundException(productId);
            repo.delete(entity.get());
        } catch (DataAccessException e){
            throw new PersistenceException("Failed to remove product",e);
        }

    }
}
