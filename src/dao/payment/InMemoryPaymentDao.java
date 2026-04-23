package dao.payment;

import domain.model.payment.Payment;

import java.util.*;

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
