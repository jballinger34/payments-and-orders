package me.jamie.paymentspractice.domain.model.payment;

public class PaymentResponse {
    private final boolean success;
    private final PaymentFailureReason failureReason;
    private final String providerReference;

    public PaymentResponse(boolean success, PaymentFailureReason authFailureReason, String providerReference){
        this.success = success;
        this.failureReason = authFailureReason;
        this.providerReference = providerReference;
    }

    public boolean isSuccessful(){
        return success;
    }
    public PaymentFailureReason getFailureReason() {
        return failureReason;
    }

    public String getProviderReference() {
        return providerReference;
    }
}
