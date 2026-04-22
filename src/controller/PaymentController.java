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

    public void run(){
        // Initial interface
        // options to:
        //  run customer interface
        //  run merchant interface
        // maybe even run issuer interface (allow issuer to see pending transactions and authorize them)
        runMerchant();
    }

    private void runMerchant() {
        // merchant interface
        // view inventory
        // view sales
        // view outstanding payments
        // view failed payments
        // handle refunds
        // exit
        boolean running = true;
        while(running){
            int choice = getMenuChoice();
            switch(choice){
                case 1:
                    manageInventory();
                    break;
                case 2:
                    viewSales();
                    break;
                case 3:
                    viewOutstanding();
                    break;
                case 4:
                    viewFailed();
                    break;
                case 5:
                    manageRefunds();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    break;

            }
        }
    }

    private void manageInventory() {
        //need to implement another menu
        // view inventory
        // restock inventory
        //

    }
    private void viewSales(){

    }
    private void viewOutstanding(){

    }
    private void viewFailed(){

    }
    private void manageRefunds(){
        //allows the merchant to go through the payments tagged for refund,
        // when they receive the return (or otherwise happy to issue refund)
        // they can confirm refund, and it'll refund to customer
    }


    private int getMenuChoice(){
        return view.displayMenuAndGetSelection();
    }

}
