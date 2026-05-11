package me.jamie.paymentspractice.domain.model.payment;

import me.jamie.paymentspractice.dto.PaymentRecord;

public class Payment {

    private final String id;
    private final double amount;
    private final PaymentMethod paymentMethod;

    private PaymentStatus status;
    private PaymentFailureReason failureReason;

    public Payment(String id, double amount, PaymentMethod method){
        this.id = id;
        this.amount = amount;
        this.paymentMethod = method;

        this.status = PaymentStatus.PENDING;
    }
    // deserialize
    public static Payment fromPersistence(String id, double amount, PaymentMethod method, PaymentStatus status, PaymentFailureReason reason){
        Payment payment = new Payment(id,amount,method);
        payment.status = status;
        payment.failureReason = reason;
        return payment;
    }
    public static Payment fromPersistence(PaymentRecord record){
        return fromPersistence(record.getId(), record.getAmount(), record.getPaymentMethod(), record.getStatus(), record.getFailureReason());
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
    public void fail(PaymentFailureReason reason){
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

    public PaymentFailureReason getFailureReason() {
        return failureReason;
    }
}
