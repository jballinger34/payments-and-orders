package me.jamie.paymentspractice.dao.order;

import java.sql.*;

import me.jamie.paymentspractice.data.entity.OrderEntity;
import me.jamie.paymentspractice.exception.OrderNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.repository.OracleDbOrderRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class OracleDbOrderDao implements OrderDao {

    private final OracleDbOrderRepository repo;

    public OracleDbOrderDao(OracleDbOrderRepository repository){
        this.repo = repository;
    }

    @Override
    public void save(OrderEntity order) throws PersistenceException {
       repo.save(order);
    }

    @Override
    public OrderEntity findByMerchantIdAndId(String merchantId, String orderId) throws PersistenceException {
        return repo.findByMerchantIdAndId(merchantId,orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<OrderEntity> findByMerchantId(String merchantId) throws PersistenceException {
        return repo.findByMerchantId(merchantId);
    }


}
