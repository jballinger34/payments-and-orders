package me.jamie.paymentspractice.exception;

/**
 * thrown when searched for payment that doesn't exist/ cant be found
 */
public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(String message) {
        super("PAYMENT", message);
    }
}
