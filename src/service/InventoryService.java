package service;

import dao.inventory.InventoryDao;

import exception.InsufficientStockException;
import exception.PersistenceException;
import exception.ProductNotFoundException;
import service.audit.AuditAction;
import service.audit.AuditService;
import service.audit.AuditType;


public class InventoryService {

    private InventoryDao inventoryDao;
    private final AuditService auditService;

    public InventoryService(InventoryDao inventoryDao, AuditService auditService){
        this.inventoryDao = inventoryDao;
        this.auditService = auditService;
    }

    public int getStockQty(String productId) throws PersistenceException, ProductNotFoundException {
        return inventoryDao.getStock(productId);
    }
    public boolean isInStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        int amtInStock = inventoryDao.getStock(productId);
        return quantity <= amtInStock;
    }
    public void reduceStock(String productId, int quantity) throws PersistenceException {
        // audit log may need to be refactored (again...) to take more detail
        // we'd like to see for example how much stock was reduced by
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);

        try{
            if(quantity <= 0) throw new IllegalArgumentException("Invalid argument, cannot reduce stock by non-positive number " + quantity );
            if(!isInStock(productId,quantity)) throw new InsufficientStockException("Insufficient stock of productId: " + productId);
            inventoryDao.removeStock(productId,quantity);
        } catch (Exception e){
            auditService.logFailure(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId, e.getMessage());
            throw e;
        }

        auditService.logSuccess(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
    }

}
