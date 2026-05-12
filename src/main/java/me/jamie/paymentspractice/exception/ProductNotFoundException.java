package me.jamie.paymentspractice.exception;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String id) {
        super("PRODUCT", id);
    }
}
