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
    public Payment getPayment(String paymentId) throws PersistenceException, PaymentNotFoundException{
        return dao.findById(paymentId);
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
