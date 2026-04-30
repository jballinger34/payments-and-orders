package stubs;

import me.jamie.paymentspractice.service.InventoryService;

public class InventoryServiceStub extends InventoryService {
    public InventoryServiceStub() {
        super(new InventoryDaoStubImpl(), new AuditServiceStub()); // not used
    }

}