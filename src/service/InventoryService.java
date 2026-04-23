package service;

import dao.InventoryDao;
import exception.PersistenceException;
import service.audit.AuditAction;
import service.audit.AuditService;
import service.audit.AuditType;

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
        // audit log may need to be refactored (again...) to take more detail
        // we'd like to see for example how much stock was reduced by
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
        dao.removeStock(productId,quantity);
        auditService.logSuccess(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
    }

}
