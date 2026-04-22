package service;

import dao.AuditDao;
import dao.PaymentDao;
import domain.model.Payment;
import domain.model.PaymentMethod;
import domain.model.PaymentStatus;
import domain.processor.PaymentProcessor;
import exception.PaymentNotFoundException;
import exception.PersistenceException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PaymentService {

    PaymentDao dao;
    AuditService auditService;
    Map<PaymentMethod,PaymentProcessor> processors;

    public PaymentService(PaymentDao dao, AuditService auditService, Map<PaymentMethod, PaymentProcessor> processors){
        this.dao = dao;
        this.auditService = auditService;
        this.processors = processors;
    }

    public Payment createPayment(double amount, PaymentMethod method) throws PersistenceException{
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment(id, amount, method);
        auditService.logPaymentCreateAttempt(payment.getId());

        dao.save(payment);
        auditService.logPaymentCreateSuccess(payment.getId());

        return payment;
    }
    public void authorizePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logPaymentAuthAttempt(payment.getId());

        processor.authorize(payment);
        //save updated state
        dao.save(payment);
        if(payment.getStatus() == PaymentStatus.AUTHORIZED){
            auditService.logPaymentAuthSuccess(payment.getId());

        } else {
            auditService.logPaymentAuthFailure(payment.getId(), payment.getFailureReason());
        }

    }
    public void authorizePayment(String paymentId) throws PersistenceException, PaymentNotFoundException {
        Payment payment = dao.findById(paymentId);
        authorizePayment(payment);
    }

    public void capturePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logPaymentCaptureAttempt(payment.getId());
        processor.capture(payment);
        dao.save(payment);
        if(payment.getStatus() == PaymentStatus.CAPTURED){
            auditService.logPaymentCaptureSuccess(payment.getId());
        } else {
            auditService.logPaymentCaptureFailure(payment.getId());
        }
    }


    public List<Payment> getAllPayments() throws PersistenceException {
        return dao.findAll();
    }


    private PaymentProcessor getValidProcessor(Payment payment) {
        if (payment == null) {
            throw new NoSuchElementException("Payment not found");
        }

        PaymentProcessor processor = processors.get(payment.getPaymentMethod());

        if (processor == null) {
            throw new IllegalArgumentException("Processor not found");
        }

        return processor;
    }

}
