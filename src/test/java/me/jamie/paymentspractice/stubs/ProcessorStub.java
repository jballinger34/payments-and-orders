package me.jamie.paymentspractice.stubs;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.processor.PaymentProcessor;

public class ProcessorStub implements PaymentProcessor {
    @Override
    public PaymentMethod supports() {
        return null;
    }

    @Override
    public void authorize(Payment payment) {

    }

    @Override
    public void capture(Payment payment) {

    }
}
