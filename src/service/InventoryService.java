package service;

import dao.InventoryDao;
import exception.PersistenceException;

public class InventoryService {

    private InventoryDao dao;
    private final AuditService auditService;

    public InventoryService(InventoryDao dao, AuditService auditService){
        this.dao = dao;
        this.auditService = auditService;
    }

    public boolean isInStock(String productId, int quantity) throws PersistenceException {
        int amtInStock = dao.getStock(productId);
        return quantity >= amtInStock;
    }
    public void reduceStock(String productId, int quantity) throws PersistenceException {
        // audit log here -- change how audit works so not written
        dao.removeStock(productId,quantity);
    }

}
