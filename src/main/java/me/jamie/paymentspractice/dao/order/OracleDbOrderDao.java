package me.jamie.paymentspractice.dao.order;

import java.sql.*;

import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;


public class OracleDbOrderDao implements OrderDao {


    public OracleDbOrderDao(String url, String user, String password){}

    @Override
    public void save(OrderRecord order) throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public OrderRecord findById(String orderId) throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<OrderRecord> findAll() throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
