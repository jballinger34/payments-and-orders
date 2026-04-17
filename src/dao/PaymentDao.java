package dao;

import domain.model.Payment;

import java.util.List;

public interface PaymentDao {

    void save(Payment payment);

    Payment findById(String id);

    List<Payment> findAll();




}
