package me.jamie.paymentspractice.stubs;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.processor.CardPaymentProcessor;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.PaymentService;

import java.util.List;
import java.util.Map;

public class PaymentServiceStub extends PaymentService {

    public PaymentServiceStub() {
        super(null, null, List.of(new ProcessorStub())); // we won't use real dependencies
    }

    @Override
    public Payment createPayment(double amount, PaymentMethod method) {
        return new Payment("TEST_PAYMENT", amount, method);
    }

    @Override
    public void authorizePayment(Payment payment) {
        payment.authorize(null);
    }

    @Override
    public void capturePayment(Payment payment) {
        payment.capture();
    }

    @Override
    public Payment getPayment(String paymentId) throws PersistenceException, PaymentNotFoundException {
        return new Payment(paymentId, 10, PaymentMethod.CARD);
    }
}