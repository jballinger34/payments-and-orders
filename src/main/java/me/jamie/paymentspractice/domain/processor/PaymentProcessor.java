package me.jamie.paymentspractice.domain.processor;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;

/**
 * PaymentProcesser talks to external systems and tells Payment what happened
 *
 */
public interface PaymentProcessor {

    PaymentMethod supports();
    Payment authorize(Payment payment);
    Payment capture(Payment payment);
}
