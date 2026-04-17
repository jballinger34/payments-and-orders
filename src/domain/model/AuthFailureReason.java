package domain.model;

/**
 * representation of what the Payment Service Provider sends back to the merchant
 */
public enum AuthFailureReason {
    INSUFFICIENT_FUNDS,
    EXPIRED_CARD,
    INCORRECT_PIN,
    TIMEOUT,
}
