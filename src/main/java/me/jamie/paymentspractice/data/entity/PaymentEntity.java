package me.jamie.paymentspractice.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;

@Entity
public class PaymentEntity {
    @Id
    private String id;
    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentFailureReason failureReason;

    public PaymentEntity(){}

    public PaymentEntity(String id, double amount, PaymentMethod paymentMethod, PaymentStatus status, PaymentFailureReason failureReason) {
        this.amount = amount;
        this.id = id;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.failureReason = failureReason;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public PaymentFailureReason getFailureReason() {
        return failureReason;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setFailureReason(PaymentFailureReason failureReason) {
        this.failureReason = failureReason;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public static PaymentEntity from(Payment payment){
        return new PaymentEntity(payment.getId(), payment.getAmount(), payment.getPaymentMethod(), payment.getStatus(), payment.getFailureReason());
    }

}
