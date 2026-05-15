package me.jamie.paymentspractice.domain.model.payment;

/**
 * representation of what the Payment Service Provider sends back to the merchant
 */
public enum PaymentFailureReason {
    /* TODO
            can flush this enum out by looking on
            https://developer.payments.jpmorgan.com/api/commerce/online-payments/online-payments/error-codes
            for potential errors, we currently just map anything either to one of these or PROCESSOR_ERROR
            if not related to other case
     */


    PROCESSOR_ERROR,
    // auth
    INSUFFICIENT_FUNDS,
    EXPIRED_CARD,
    INCORRECT_CVV,
    TIMEOUT,

    //capture
    AUTH_EXPIRED,
    FRAUD_SUSPECTED,
    CARD_BLOCKED,
    ;
}
