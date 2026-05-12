package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.repository.OracleDbInventoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public class OracleDbInventoryDao implements InventoryDao {

    OracleDbInventoryRepository repo;

    public OracleDbInventoryDao(OracleDbInventoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product findById(String productId) throws PersistenceException, ProductNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<Product> findAll() throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void put(String productId, Product product) throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void remove(String productId) throws PersistenceException, ProductNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
