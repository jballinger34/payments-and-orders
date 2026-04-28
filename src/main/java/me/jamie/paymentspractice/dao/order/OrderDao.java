package me.jamie.paymentspractice.dao.order;

import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;

public interface OrderDao {
    void save(Order order) throws PersistenceException;
    Order findById(String orderId);
    List<Order> findAll();
}
