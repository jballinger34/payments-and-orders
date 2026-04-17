package controller;

import domain.model.Payment;
import domain.model.PaymentMethod;
import service.PaymentService;
import view.PaymentView;

public class PaymentController {

    private PaymentView view;
    private final PaymentService service;

    public PaymentController(PaymentView view, PaymentService paymentService) {
        this.view = view;
        this.service = paymentService;
    }


    public void run(){
        for(int i = 0; i < 3; i++){
            Payment payment = service.createPayment(100,PaymentMethod.CARD);
            service.authorizePayment(payment);
            //view.displayPayment(payment);
        }
        view.displayAllPayments(service.getAllPayments());


    }
}
