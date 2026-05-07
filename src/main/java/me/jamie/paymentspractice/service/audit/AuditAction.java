package me.jamie.paymentspractice.service.audit;

public enum AuditAction {
    // general
    CREATE,

    //payment
    AUTH,
    CAPTURE,

    //order

    //inventory
    SET_NAME,
    SET_COST,
    SET_STOCK,
    REDUCE_STOCK,
    RESTOCK,
    ;


    @Override
    public String toString() {
        return super.toString() + ":";
    }
}
