package service;

import dao.payment.PaymentDao;
import domain.model.payment.Payment;
import domain.model.payment.PaymentMethod;
import domain.model.payment.PaymentStatus;
import domain.processor.PaymentProcessor;
import exception.PaymentNotFoundException;
import exception.PersistenceException;
import service.audit.AuditAction;
import service.audit.AuditService;
import service.audit.AuditType;

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
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.CREATE, payment.getId());

        dao.save(payment);
        auditService.logSuccess(AuditType.PAYMENT, AuditAction.CREATE, payment.getId());

        return payment;
    }
    public void authorizePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.AUTH, payment.getId());

        processor.authorize(payment);
        //save updated state
        dao.save(payment);
        if(payment.getStatus() == PaymentStatus.AUTHORIZED){
            auditService.logSuccess(AuditType.PAYMENT, AuditAction.AUTH, payment.getId());

        } else {
            auditService.logFailure(AuditType.PAYMENT,AuditAction.AUTH,payment.getId(), payment.getFailureReason().toString());
        }

    }
    public void authorizePayment(String paymentId) throws PersistenceException, PaymentNotFoundException {
        Payment payment = dao.findById(paymentId);
        authorizePayment(payment);
    }

    public void capturePayment(Payment payment) throws PersistenceException {
        PaymentProcessor processor = getValidProcessor(payment);
        auditService.logAttempt(AuditType.PAYMENT, AuditAction.CAPTURE, payment.getId());
        processor.capture(payment);
        dao.save(payment);
        if(payment.getStatus() == PaymentStatus.CAPTURED){
            auditService.logSuccess(AuditType.PAYMENT, AuditAction.CAPTURE, payment.getId());
        } else {
            auditService.logFailure(AuditType.PAYMENT, AuditAction.CAPTURE, payment.getId(), "NO_REASON_YET");
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
