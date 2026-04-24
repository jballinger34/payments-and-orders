package domain.model.payment;

/**
 * representation of what the Payment Service Provider sends back to the merchant
 */
public enum PaymentFailureReason {
    // auth
    INSUFFICIENT_FUNDS,
    EXPIRED_CARD,
    INCORRECT_PIN,
    TIMEOUT,

    //capture
    AUTH_EXPIRED,
    FRAUD_SUSPECTED,
    CARD_BLOCKED,
    ;
}
