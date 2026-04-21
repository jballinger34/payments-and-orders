package view;

import domain.model.Payment;
import domain.model.PaymentStatus;

import java.util.List;

public class PaymentView {

    private UserIO io;

    public PaymentView(UserIO io){
        this.io = io;
    }

    public void displayPayment(Payment payment){
        io.print("=== Payment ID: " +payment.getId()+ " ===");
        io.print("--- Amount: " + payment.getAmount() + " ---");
        io.print("--- Method: " + payment.getPaymentMethod() + " ---");
        io.print("--- Status: " + payment.getStatus() + " ---");
        if(payment.getStatus() == PaymentStatus.FAILED){
            io.print("--- Failed: " + payment.getFailureReason() + " ---");
        }

    }

    public void displayAllPayments(List<Payment> payments){
        io.print("=== Display All Payments ===");
        for(Payment payment : payments){
            displayPayment(payment);
        }
        io.print("========================");
    }

    public void displayError(String message){
        io.print("ERROR: " + message);
    }

    public void setIo(UserIO io) {
        this.io = io;
    }
}
