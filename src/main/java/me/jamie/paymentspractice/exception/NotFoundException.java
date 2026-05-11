package me.jamie.paymentspractice.exception;

public abstract class NotFoundException extends RuntimeException {

    private final String entityType;

    public NotFoundException(String entityType, String message) {
        super(message);
        this.entityType = entityType;
    }
    public String getEntityType(){
        return entityType;
    }
}
