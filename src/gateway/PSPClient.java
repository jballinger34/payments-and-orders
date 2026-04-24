package gateway;

import domain.model.payment.PaymentResponse;
import domain.model.payment.Payment;

/**
 * Sends requests to external payment service provider
 *
 * Implementation would provide methods for telling Stripe or PayPal
 * "i need to auth this" or "i want to capture this"
 */
public interface PSPClient {

    /**
     * implementation to call an external psp and get result, translate into AuthResult return
     *
     *
     * @param payment send payment id and info to PSP
     * @return AuthResponse, models the response from an external PSP
     */
    PaymentResponse authorize(Payment payment);

    PaymentResponse capture(Payment payment);


}
