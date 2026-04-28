package me.jamie.paymentspractice.service.audit;

public enum AuditAction {
    // general
    CREATE,

    //payment
    AUTH,
    CAPTURE,

    //order

    //inventory
    REDUCE_STOCK,
    ;


    @Override
    public String toString() {
        return super.toString() + ":";
    }
}
