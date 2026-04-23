package dao.order;

import domain.model.order.Order;
import exception.PersistenceException;

import java.util.List;

public interface OrderDao {
    void save(Order order) throws PersistenceException;
    Order findById(String orderId);
    List<Order> findAll();
}
