package domain.model.payment;

public class Payment {

    private final String id;
    private final double amount;
    private final PaymentMethod paymentMethod;

    private PaymentStatus status;
    private AuthFailureReason failureReason;

    //create a new payment
    public Payment(String id, double amount, PaymentMethod method){
        this.id = id;
        this.amount = amount;
        this.paymentMethod = method;

        this.status = PaymentStatus.PENDING;
    }
    // deserialize a failed payment
    public static Payment fromPersistance(String id, double amount, PaymentMethod method, PaymentStatus status, AuthFailureReason reason){
        Payment payment = new Payment(id,amount,method);
        payment.status = status;
        payment.failureReason = reason;
        return payment;
    }



    public void authorize(){
        if(status != PaymentStatus.PENDING){
            throw new IllegalStateException("Only PENDING payments can be authorized");
        }
        this.status = PaymentStatus.AUTHORIZED;
    }
    public void capture(){
        if(status != PaymentStatus.AUTHORIZED){
            throw new IllegalStateException("Can only capture an AUTHORISED payment");
        }
        this.status = PaymentStatus.CAPTURED;
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
