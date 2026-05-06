package me.jamie.paymentspractice.domain.processor;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;

/**
 * PaymentProcesser talks to external systems and tells Payment what happened
 *
 */
public interface PaymentProcessor {

    PaymentMethod supports();
    void authorize(Payment payment);
    void capture(Payment payment);
}
