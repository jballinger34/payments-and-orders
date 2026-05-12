package me.jamie.paymentspractice.exception;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(String id) {
        super("ORDER", id);
    }
}
