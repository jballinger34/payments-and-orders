package me.jamie.paymentspractice.dao.payment;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;

import java.util.List;

public interface PaymentDao {

    void save(Payment payment) throws PersistenceException;

    Payment findById(String id) throws PaymentNotFoundException, PersistenceException;

    List<Payment> findAll() throws PersistenceException;




}
