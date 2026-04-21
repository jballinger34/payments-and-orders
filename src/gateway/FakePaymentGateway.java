package gateway;

import domain.model.AuthFailureReason;
import domain.model.AuthResponse;
import domain.model.Payment;

import java.util.Random;

public class FakePaymentGateway implements PaymentGateway {

    private final Random random = new Random();

    @Override
    public AuthResponse authorize(Payment payment) {
        boolean success = random.nextBoolean();
        if(success){
            return new AuthResponse(true, null);
        } else {
            int randomReason = random.nextInt(AuthFailureReason.values().length);
            return new AuthResponse(false, AuthFailureReason.values()[randomReason]);
        }
    }

    @Override
    public void capture(Payment payment) {

    }


    @Override
    public void onCleared() {

    }

    @Override
    public void onSettled() {

    }
}
