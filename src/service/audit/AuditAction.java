package service.audit;

public enum AuditAction {
    // general
    CREATE,

    //payment
    AUTH,
    CAPTURE,

    //order
    PROCESS_PAYMENT,

    //inventory
    REDUCE_STOCK,
}
