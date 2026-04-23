package service;

import dao.inventory.InventoryDao;
import dao.product.ProductDao;
import domain.model.LineItem;
import domain.model.Product;
import exception.PersistenceException;
import service.audit.AuditAction;
import service.audit.AuditService;
import service.audit.AuditType;

import java.util.Arrays;
import java.util.List;

public class InventoryService {

    private ProductDao productDao;
    private InventoryDao inventoryDao;
    private final AuditService auditService;

    public InventoryService(ProductDao productDao, InventoryDao inventoryDao, AuditService auditService){
        this.productDao = productDao;
        this.inventoryDao = inventoryDao;
        this.auditService = auditService;
    }

    public boolean isInStock(String productId, int quantity) throws PersistenceException {
        int amtInStock = inventoryDao.getStock(productId);
        return quantity <= amtInStock;
    }
    public void reduceStock(String productId, int quantity) throws PersistenceException {
        // audit log may need to be refactored (again...) to take more detail
        // we'd like to see for example how much stock was reduced by
        auditService.logAttempt(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
        inventoryDao.removeStock(productId,quantity);
        auditService.logSuccess(AuditType.INVENTORY, AuditAction.REDUCE_STOCK, productId);
    }

    //temporary test method, gets the items hard coded in InMemoryProductDao
    // just so i dont have to implement a file dao for the moment
    public List<LineItem> getSampleItems(){
        Product apple = productDao.findById("0");
        Product orange = productDao.findById("1");
        LineItem bagOfApples = new LineItem(apple,4);
        LineItem singleOrange = new LineItem(orange,1);

        return Arrays.asList(bagOfApples, singleOrange);
    }

}
