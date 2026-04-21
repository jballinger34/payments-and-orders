package service;

import dao.AuditDao;
import dao.PaymentDao;
import domain.model.Payment;
import domain.model.PaymentMethod;
import domain.model.PaymentStatus;
import domain.processor.PaymentProcessor;
import exception.PersistenceException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PaymentService {

    PaymentDao dao;
    AuditDao auditDao;
    Map<PaymentMethod,PaymentProcessor> processors;

    public PaymentService(PaymentDao dao, AuditDao auditDao, Map<PaymentMethod, PaymentProcessor> processors){
        this.dao = dao;
        this.auditDao = auditDao;
        this.processors = processors;
    }

    public Payment createPayment(double amount, PaymentMethod method) throws PersistenceException{
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment(id, amount, method);
        auditDao.writeEntry("Attempting to CREATE payment: " + payment.getId());
        dao.save(payment);
        auditDao.writeEntry("Payment:" + payment.getId() + " CREATED");

        return payment;
    }
    public void authorizePayment(Payment payment) throws PersistenceException {
        if(payment == null){
            throw new NoSuchElementException("Payment not found");
        }

        PaymentProcessor processor = processors.get(payment.getPaymentMethod());
        if(processor == null){
            throw new IllegalArgumentException("Processor not found");
        }
        auditDao.writeEntry("Attempting to AUTHORIZE payment: " + payment.getId());
        processor.authorize(payment);
        //save updated state
        dao.save(payment);
        if(payment.getStatus() == PaymentStatus.AUTHORIZED){
            auditDao.writeEntry("Payment"+ payment.getId() + " AUTHORIZED");
        } else {
            auditDao.writeEntry("Payment: "+ payment.getId()+" AUTHORIZE FAILED:"+ payment.getFailureReason());
        }

    }
    public void authorizePayment(String paymentId) throws PersistenceException {
        Payment payment = dao.findById(paymentId);
        authorizePayment(payment);
    }
    public List<Payment> getAllPayments() throws PersistenceException {
        return dao.findAll();
    }




}
