package domain.processor;

import domain.model.Payment;

/**
 * PaymentProcesser talks to external systems and tells Payment what happened
 *
 */
public interface PaymentProcessor {

    void authorize(Payment payment);
    void capture(Payment payment);
}
