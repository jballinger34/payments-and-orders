package gateway;

import domain.model.payment.PaymentFailureReason;
import domain.model.payment.PaymentResponse;
import domain.model.payment.Payment;

import java.util.Random;

public class FakePaymentGateway implements PaymentGateway {

    private final Random random = new Random();

    @Override
    public PaymentResponse authorize(Payment payment) {
        boolean success = random.nextBoolean();
        if(success){
            return new PaymentResponse(true, null);
        } else {
            //first 4 reasons are to do with auth
            int randomReason = random.nextInt(4);
            return new PaymentResponse(false, PaymentFailureReason.values()[randomReason]);
        }
    }

    @Override
    public PaymentResponse capture(Payment payment) {
        return new PaymentResponse(true, null);
    }


    @Override
    public void onCleared() {
        throw new UnsupportedOperationException("onCleared not yet implemented");
    }

    @Override
    public void onSettled() {
        throw new UnsupportedOperationException("onSettled not yet implemented");
    }
}
