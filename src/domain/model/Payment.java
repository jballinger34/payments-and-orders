package domain.model;

public class Payment {

    private final String id;
    private final double amount;
    private final PaymentMethod paymentMethod;

    private PaymentStatus status;
    private AuthFailureReason failureReason;

    public Payment(String id, double amount, PaymentMethod method){
        this.id = id;
        this.amount = amount;
        this.paymentMethod = method;

        this.status = PaymentStatus.PENDING;
    }

    public void authorize(){
        if(status != PaymentStatus.PENDING){
            throw new IllegalStateException("Only PENDING payments can be authorized");
        }
        this.status = PaymentStatus.AUTHORIZED;
    }
    public void fail(AuthFailureReason reason){
        if(status == PaymentStatus.CAPTURED){
            throw new IllegalStateException("Cannot fail a CAPTURED payment");
        }
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public AuthFailureReason getFailureReason() {
        return failureReason;
    }
}
