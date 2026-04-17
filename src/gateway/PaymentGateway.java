package gateway;

import domain.model.AuthResponse;
import domain.model.Payment;

/**
 * Adapter to external payment service provider - here we just use a FakePaymentGateway
 *
 */
public interface PaymentGateway {

    /**
     * implementation to call an external psp and get result, translate into AuthResult return
     *
     *
     * @param payment send payment id and info to PSP
     * @return AuthResponse, models the response from an external PSP
     */
    AuthResponse authorize(Payment payment);


}
