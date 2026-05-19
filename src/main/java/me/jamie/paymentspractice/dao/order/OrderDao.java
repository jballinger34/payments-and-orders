package me.jamie.paymentspractice.dao.order;

import me.jamie.paymentspractice.data.entity.OrderEntity;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;

public interface OrderDao {
    void save(OrderEntity order) throws PersistenceException;
    OrderEntity findByMerchantIdAndId(String merchantId, String orderId) throws PersistenceException;
    List<OrderEntity> findByMerchantId(String merchantId) throws PersistenceException;
}
