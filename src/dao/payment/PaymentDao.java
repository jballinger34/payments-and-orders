package dao.payment;

import domain.model.payment.Payment;
import exception.PaymentNotFoundException;
import exception.PersistenceException;

import java.util.List;

public interface PaymentDao {

    void save(Payment payment) throws PersistenceException;

    Payment findById(String id) throws PaymentNotFoundException, PersistenceException;

    List<Payment> findAll() throws PersistenceException;




}
