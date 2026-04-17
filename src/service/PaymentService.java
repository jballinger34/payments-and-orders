package service;

import dao.PaymentDao;
import domain.model.Payment;
import domain.model.PaymentMethod;
import domain.processor.PaymentProcessor;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PaymentService {

    PaymentDao dao;
    Map<PaymentMethod,PaymentProcessor> processors;

    public PaymentService(PaymentDao dao, Map<PaymentMethod,PaymentProcessor> processors){
        this.dao = dao;
        this.processors = processors;
    }

    public Payment createPayment(double amount, PaymentMethod method){
        String id = UUID.randomUUID().toString();
        Payment payment = new Payment(id, amount, method);

        dao.save(payment);
        return payment;
    }
    public void authorizePayment(Payment payment){
        if(payment == null){
            throw new NoSuchElementException("Payment not found");
        }

        PaymentProcessor processor = processors.get(payment.getPaymentMethod());
        if(processor == null){
            throw new IllegalArgumentException("Processor not found");
        }
        processor.authorize(payment);

        //save updated state
        dao.save(payment);
    }
    public void authorizePayment(String paymentId){
        Payment payment = dao.findById(paymentId);
        authorizePayment(payment);
    }
    public List<Payment> getAllPayments(){
        return dao.findAll();
    }




}
