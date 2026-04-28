package me.jamie.paymentspractice.dao.order;

import me.jamie.paymentspractice.domain.model.order.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryOrderDao implements OrderDao {
    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
