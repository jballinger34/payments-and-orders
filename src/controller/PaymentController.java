package controller;

import domain.model.Payment;
import domain.model.PaymentMethod;
import domain.model.PaymentStatus;
import exception.PersistenceException;
import service.PaymentService;
import view.PaymentView;

public class PaymentController {

    private final PaymentView view;
    private final PaymentService service;

    public PaymentController(PaymentView view, PaymentService paymentService) {
        this.view = view;
        this.service = paymentService;
    }


    public void run() {
        try{
            for(int i = 0; i < 3; i++){
                Payment payment = service.createPayment(100,PaymentMethod.CARD);
                service.authorizePayment(payment);
                if(payment.getStatus() == PaymentStatus.AUTHORIZED){
                    service.capturePayment(payment);
                }
                view.displayPayment(payment);
            }
            view.displayAllPayments(service.getAllPayments());
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
        }

    }

}
