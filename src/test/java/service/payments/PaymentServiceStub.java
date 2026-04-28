package service.payments;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.service.PaymentService;

public class PaymentServiceStub extends PaymentService {

    public PaymentServiceStub() {
        super(null, null, null); // we won't use real dependencies
    }

    @Override
    public Payment createPayment(double amount, PaymentMethod method) {
        return new Payment("PAYMENT_1", amount, method);
    }

    @Override
    public void authorizePayment(Payment payment) {
        payment.authorize();
    }

    @Override
    public void capturePayment(Payment payment) {
        payment.capture();
    }
}