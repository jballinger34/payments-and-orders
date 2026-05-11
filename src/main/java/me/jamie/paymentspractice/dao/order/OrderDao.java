package me.jamie.paymentspractice.dao.order;

import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;

public interface OrderDao {
    void save(OrderRecord order) throws PersistenceException;
    OrderRecord findById(String orderId) throws PersistenceException;
    List<OrderRecord> findAll() throws PersistenceException;
}
