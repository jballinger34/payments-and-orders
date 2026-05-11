package me.jamie.paymentspractice.exception;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String message) {
        super("PRODUCT", message);
    }
}
