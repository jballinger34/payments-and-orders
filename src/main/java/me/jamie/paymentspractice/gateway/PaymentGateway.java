package me.jamie.paymentspractice.gateway;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;

/**
 * Sends requests to external payment service provider
 *
 * Implementation would provide methods for telling JPM, Stripe or PayPal
 * "i need to auth this" or "i want to capture this"
 */
public interface PaymentGateway {

    /**
     * implementation to call an external psp to authorize the payment and get result, translate into PaymentResponse
     * which holds boolean for success, an external id and a reason for failure
     *
     *
     * @param payment send payment id and info to PSP
     * @return PaymentResponse, mapped response from an external PSP
     */
    PaymentResponse authorize(Payment payment);

    /**
     * implementation to call an external psp to capture the payment and get result, translate into PaymentResponse
     * which holds boolean for success, an external id and a reason for failure
     *
     *
     * @param payment send payment id and info to PSP
     * @return PaymentResponse, mapped response from an external PSP
     */
    PaymentResponse capture(Payment payment);
}
