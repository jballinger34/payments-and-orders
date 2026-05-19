package me.jamie.paymentspractice.exception;

public class MerchantNotFoundException extends NotFoundException {
    public MerchantNotFoundException(String id) {
        super("MERCHANT", id);
    }
}
