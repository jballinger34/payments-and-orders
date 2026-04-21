package gateway;

import domain.model.AuthResponse;
import domain.model.Payment;

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
    AuthResponse authorize(Payment payment);

    void capture(Payment payment);


}
