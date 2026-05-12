package me.jamie.paymentspractice.exception;

public abstract class NotFoundException extends RuntimeException {

    private final String entityType;

    public NotFoundException(String entityType, String entityId) {
        super(entityType + " not found. id: " + entityId);
        this.entityType = entityType;
    }
    public String getEntityType(){
        return entityType;
    }
}
