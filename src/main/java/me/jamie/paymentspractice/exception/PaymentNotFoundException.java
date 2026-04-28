package me.jamie.paymentspractice.exception;

/**
 * thrown when searched for payment that doesn't exist/ cant be found
 */
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
