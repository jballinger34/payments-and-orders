package me.jamie.paymentspractice.domain.model.payment;

import me.jamie.paymentspractice.data.entity.PaymentEntity;

public class Payment {

    private final String id;
    private final double amount;
    private final PaymentMethod paymentMethod;

    //provider reference is the reference that the provider api returns to us
    private String providerReference;

    private PaymentStatus status;
    private PaymentFailureReason failureReason;

    public Payment(String id, double amount, PaymentMethod method){
        this.id = id;
        this.amount = amount;
        this.paymentMethod = method;

        this.status = PaymentStatus.PENDING;
    }
    // deserialize
    public static Payment fromPersistence(String id, double amount, PaymentMethod method, String providerReference, PaymentStatus status, PaymentFailureReason reason){
        Payment payment = new Payment(id,amount,method);
        payment.providerReference = providerReference;

        payment.status = status;
        payment.failureReason = reason;
        return payment;
    }
    public static Payment fromPersistence(PaymentEntity record){
        return fromPersistence(record.getId(), record.getAmount(), record.getPaymentMethod(), record.getProviderReference(),record.getStatus(), record.getFailureReason());
    }


    public void authorize(String providerReference){
        if(status != PaymentStatus.PENDING){
            throw new IllegalStateException("Only PENDING payments can be authorized");
        }
        if(providerReference != null) {
            this.providerReference = providerReference;
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

    public String getProviderReference() {
        return providerReference;
    }

    public PaymentFailureReason getFailureReason() {
        return failureReason;
    }
}
