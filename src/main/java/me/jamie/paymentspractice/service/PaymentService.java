package me.jamie.paymentspractice.service;


import me.jamie.paymentspractice.dao.payment.PaymentDao;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.domain.processor.PaymentProcessor;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    /*
     TODO the gateway doesnt check if the payment is in correct state.
     imagine this:
            i have a pending payment (needs auth)
            i try call gateway.auth and it fails,
            but i don't realise so i call gateway.capture.
            jpm realises this payment isnt authed so it auths it then captures it and returns a success response
            my paymentprocessor tries to update the state and fails (because on my system its still pending).
            in jpm system that payment is authed+captured. in mine its failed.
     need to add relevant checks in the correct places.

     flow looks like:
     orderservice place order
     payment service create payment
     payment processor authorise
     gateway authorise returns PaymentResponse
     payment processor calls payment auth/fail based on response
     payment validates state and mutates if valid

     currently we check state in orderservice (reserve stock and capture calls)
     and in payment class

     the checks in orderservice should be good for now
     as thats the only way paymentservice and payment gateway authorise/capture get called
     but in the future (if we let manual capture happen)

     we need to check for state BEFORE we call the gateway methods
     */


    private final PaymentDao dao;
    private final AuditService auditService;
    private final Map<PaymentMethod, PaymentProcessor> processors;

    public PaymentService(PaymentDao dao, AuditService auditService, List<PaymentProcessor> processorList){
        this.dao = dao;
        this.auditService = auditService;
        this.processors = processorList.stream().collect(Collectors.toMap(PaymentProcessor::supports, p -> p));
    }

    public Payment createPayment(double amount, PaymentMethod method) throws PersistenceException{
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment(id, amount, method);
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.CREATE, payment.getId());

        dao.save(payment);
        auditService.logSuccess(AuditType.PAYMENT, AuditAction.CREATE, payment.getId());

        return payment;
    }
    public void authorizePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.AUTH, payment.getId());

        Payment updated = processor.authorize(payment);
        //save updated state
        dao.save(updated);
        if(updated.getStatus() == PaymentStatus.AUTHORIZED){
            auditService.logSuccess(AuditType.PAYMENT, AuditAction.AUTH, updated.getId());
        } else {
            String reason = updated.getFailureReason() != null ? updated.getFailureReason().toString() : "UNKNOWN";
            auditService.logFailure(AuditType.PAYMENT,AuditAction.AUTH,updated.getId(), reason);
        }
    }
    public void authorizePayment(String paymentId) throws PersistenceException, PaymentNotFoundException {
        Payment payment = dao.findById(paymentId);
        authorizePayment(payment);
    }

    public void capturePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.CAPTURE, payment.getId());
        Payment updated = processor.capture(payment);
        dao.save(updated);
        if(updated.getStatus() == PaymentStatus.CAPTURED){
            auditService.logSuccess(AuditType.PAYMENT, AuditAction.CAPTURE, updated.getId());
        } else {
            String reason = updated.getFailureReason() != null ? updated.getFailureReason().toString() : "UNKNOWN";
            auditService.logFailure(AuditType.PAYMENT, AuditAction.CAPTURE, updated.getId(), reason);
        }
    }


    public List<Payment> getAllPayments() throws PersistenceException {
        return dao.findAll();
    }
    public Payment getPayment(String paymentId) throws PersistenceException, PaymentNotFoundException{
        return dao.findById(paymentId);
    }


    private PaymentProcessor getValidProcessor(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found") ;
        }

        PaymentProcessor processor = processors.get(payment.getPaymentMethod());

        if (processor == null) {
            throw new IllegalArgumentException("Processor not found");
        }

        return processor;
    }

}
