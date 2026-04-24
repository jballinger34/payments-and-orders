package domain.model.payment;

public class PaymentResponse {
    private final boolean success;
    private final PaymentFailureReason failureReason;

    public PaymentResponse(boolean success, PaymentFailureReason authFailureReason){
        this.success = success;
        this.failureReason = authFailureReason;
    }

    public boolean isSuccessful(){
        return success;
    }
    public PaymentFailureReason getFailureReason() {
        return failureReason;
    }
}
