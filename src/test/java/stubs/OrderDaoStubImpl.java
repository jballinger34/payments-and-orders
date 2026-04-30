package stubs;

import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.util.*;

public class OrderDaoStubImpl implements OrderDao {

    Map<String, OrderRecord> records = new HashMap<>();
    @Override
    public void save(OrderRecord order) throws PersistenceException {
        records.put(order.id(), order);
    }

    @Override
    public OrderRecord findById(String orderId) throws ProductNotFoundException {
        if(!records.containsKey(orderId)){
            throw new ProductNotFoundException("Order doesn't exist.");
        }
        return records.get(orderId);
    }

    @Override
    public List<OrderRecord> findAll() {
        return new ArrayList<>(records.values());
    }
}
