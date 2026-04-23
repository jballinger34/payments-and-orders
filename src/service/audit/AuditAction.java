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
    ;


    @Override
    public String toString() {
        return super.toString() + ":";
    }
}
