package me.jamie.paymentspractice.dao.payment;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.data.entity.PaymentEntity;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.repository.OracleDbPaymentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class OracleDbPaymentDao implements PaymentDao {

    OracleDbPaymentRepository repo;

    public OracleDbPaymentDao(OracleDbPaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(Payment payment) throws PersistenceException {
        repo.save(PaymentEntity.from(payment));
    }

    @Override
    public Payment findById(String id) throws PaymentNotFoundException, PersistenceException {
        Optional<PaymentEntity> record = repo.findById(id);
        if (record.isEmpty()) throw new PaymentNotFoundException(id);

        return Payment.fromPersistence(record.get());
    }

    @Override
    public List<Payment> findAll() throws PersistenceException {
        return repo.findAll().stream().map(Payment::fromPersistence).toList();
    }
}
