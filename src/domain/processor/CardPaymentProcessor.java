package domain.processor;

import domain.model.payment.PaymentResponse;
import domain.model.payment.Payment;
import gateway.PaymentGateway;

public class CardPaymentProcessor implements PaymentProcessor {

    PaymentGateway gateway;

    public CardPaymentProcessor(PaymentGateway gateway){
        this.gateway = gateway;
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
