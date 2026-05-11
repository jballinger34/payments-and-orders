package me.jamie.paymentspractice.dao.payment;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.repository.OracleDbPaymentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public class OracleDbPaymentDao implements PaymentDao {

    OracleDbPaymentRepository repo;

    public OracleDbPaymentDao(OracleDbPaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(Payment payment) throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Payment findById(String id) throws PaymentNotFoundException, PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<Payment> findAll() throws PersistenceException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
