package me.jamie.paymentspractice.domain.processor;


import me.jamie.paymentspractice.domain.model.payment.Payment;

/**
 * PaymentProcesser talks to external systems and tells Payment what happened
 *
 */
public interface PaymentProcessor {

    void authorize(Payment payment);
    void capture(Payment payment);
}
