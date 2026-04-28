package service.inventory;

import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;

import java.util.HashMap;
import java.util.Map;

public class InventoryServiceStub extends InventoryService {

    private Map<String, Integer> stock = new HashMap<>();

    public InventoryServiceStub() {
        super(null, null); // not used
        stock.put("ITEM_1", 10);
        stock.put("ITEM_2", 0);
    }

    @Override
    public int getStockQty(String productId) throws ProductNotFoundException {
        return stock.get(productId);
    }



    @Override
    public boolean isInStock(String productId, int quantity) throws ProductNotFoundException {
        return getStockQty(productId) >= quantity;
    }

    @Override
    public void reduceStock(String productId, int quantity) {
        int current = stock.get(productId);
        stock.put(productId, current - quantity);
    }
}