package me.jamie.paymentspractice.service;

import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final AuditService auditService;

    public InventoryService(InventoryDao inventoryDao, AuditService auditService){
        this.inventoryDao = inventoryDao;
        this.auditService = auditService;
    }

    public int getStockQty(String merchantId, String productId) throws PersistenceException, ProductNotFoundException {
        return inventoryDao.getStock(merchantId, productId);
    }
    public boolean isInStock(String merchantId, String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        int amtInStock = inventoryDao.getStock(merchantId,productId);
        return quantity <= amtInStock;
    }
    public Product createProduct(String merchantId, String name, double cost, int stock) throws PersistenceException {
        if(stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        if(cost < 0) throw new IllegalArgumentException("Cost cannot be negative");

        String id = UUID.randomUUID().toString();
        Product product = new Product(id, merchantId, name, cost, stock);
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.CREATE, id);

        try {
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.CREATE, id);
            return product;

        } catch (Exception e) {
            auditService.logFailure(AuditType.INVENTORY, AuditAction.CREATE, id, e.getMessage());
            throw e;
        }

    }
    public Product setName(String merchantId, String productId, String name) throws PersistenceException, ProductNotFoundException {
        Product product = Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.SET_NAME, productId);

        try {
            product.setName(name);
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.SET_NAME, productId);
            return product;

        } catch (Exception e) {
            auditService.logFailure(AuditType.INVENTORY, AuditAction.SET_NAME, productId, e.getMessage());
            throw e;
        }

    }
    public Product setPrice(String merchantId, String productId, double price) throws PersistenceException, ProductNotFoundException {
        if (price < 0) throw new IllegalArgumentException("Cost cannot be negative");
        Product product = Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.SET_COST, productId);

        try {
            product.setCost(price);
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.SET_COST, productId);
            return product;

        } catch (Exception e) {
            auditService.logFailure(AuditType.INVENTORY, AuditAction.SET_COST, productId, e.getMessage());
            throw e;
        }

    }
    public Product setStock(String merchantId, String productId, int stock) throws PersistenceException, ProductNotFoundException {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");

        Product product = Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.SET_STOCK, productId);

        try {
            product.setStock(stock);
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.SET_STOCK, productId);
            return product;

        } catch (Exception e) {
            auditService.logFailure(AuditType.INVENTORY, AuditAction.SET_STOCK, productId, e.getMessage());
            throw e;
        }
    }

    public Product restockProduct(String merchantId, String productId, int amount) throws PersistenceException, ProductNotFoundException{
        if(amount <= 0) throw new IllegalArgumentException("Restock amount must be positive.");
        Product product = Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.RESTOCK, productId);
        try{
            product.setStock(product.getStock()+amount);
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.RESTOCK, productId);
            return product;
        } catch (Exception e){
            auditService.logFailure(AuditType.INVENTORY, AuditAction.RESTOCK, productId, e.getMessage());
            throw e;
        }

    }

    public Product reduceStock(String merchantId, String productId, int quantity) throws PersistenceException {
        if(quantity <= 0) throw new IllegalArgumentException("Invalid argument, cannot reduce stock by non-positive number " + quantity );
        Product product = Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
        if(!isInStock(merchantId,productId,quantity)) throw new InsufficientStockException("Insufficient stock of product, id: " + productId);
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);

        try{
            product.setStock(product.getStock() - quantity);
            inventoryDao.save(ProductEntity.from(product));
            auditService.logSuccess(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
            return product;
        } catch (Exception e){
            auditService.logFailure(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId, e.getMessage());
            throw e;
        }
    }
    public List<Product> getAllProducts(String merchantId) throws PersistenceException {
        return inventoryDao.findByMerchantId(merchantId).stream().map(Product::fromPersistence).toList();

    }
    public Product getProduct(String merchantId, String productId) throws PersistenceException, ProductNotFoundException{
        return Product.fromPersistence(inventoryDao.findByMerchantIdAndId(merchantId, productId));
    }

    public void deleteProduct(String merchantId, String productId) throws PersistenceException {
        inventoryDao.remove(merchantId, productId);
    }

}
