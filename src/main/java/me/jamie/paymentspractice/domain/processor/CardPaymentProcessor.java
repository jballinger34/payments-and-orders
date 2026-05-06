package me.jamie.paymentspractice.domain.processor;

import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;
import me.jamie.paymentspractice.gateway.PaymentGateway;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentProcessor implements PaymentProcessor {

    PaymentGateway gateway;

    public CardPaymentProcessor(PaymentGateway gateway){
        this.gateway = gateway;
    }

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CARD;
    }

    @Override
    public void authorize(Payment payment) {
        PaymentResponse authResponse = gateway.authorize(payment);

        if(authResponse.isSuccessful()){
            payment.authorize();
        } else {
            payment.fail(authResponse.getFailureReason());
        }

    }

    @Override
    public void capture(Payment payment) {
        PaymentResponse captureResponse = gateway.capture(payment);
        if(captureResponse.isSuccessful()){
            payment.capture();
        } else {
            payment.fail(captureResponse.getFailureReason());
        }
    }
}
