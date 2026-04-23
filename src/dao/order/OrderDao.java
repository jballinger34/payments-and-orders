package dao.order;

import domain.model.order.Order;

import java.util.List;

public interface OrderDao {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findAll();
}
