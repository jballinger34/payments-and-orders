package me.jamie.paymentspractice.dao.payment;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryPaymentDao implements PaymentDao {

    private final Map<String, Payment> payments = new HashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getId(),payment);
    }

    @Override
    public Payment findById(String id) {
        return payments.get(id);
    }

    @Override
    public List<Payment> findAll(){
        return new ArrayList<>(payments.values());
    }

}
