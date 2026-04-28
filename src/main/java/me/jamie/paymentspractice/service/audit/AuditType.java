package me.jamie.paymentspractice.service.audit;

public enum AuditType {
    PAYMENT,
    ORDER,
    INVENTORY,
    ;


    @Override
    public String toString() {
        return super.toString() + ":";
    }
}
