package me.jamie.paymentspractice.controller;


import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.OrderService;
import me.jamie.paymentspractice.view.MerchantView;

import java.util.List;

public class MerchantController implements Controller {

    // TODO transform into REST CONTROLLER

    private final MerchantView view;
    private final OrderService orderService;

    public MerchantController(MerchantView view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    public void run() {
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
                    viewOrders();
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

    @Override
    public String getName() {
        return "Merchant Mode";
    }

    private void manageInventory() {
        //need to implement InventoryController
        // view inventory
        // restock inventory
        //
        view.displayError("Not implemented yet");
    }
    private void viewOrders() {
        try{
            List<Order> orders = orderService.getAllOrders();
            view.displayAllOrders(orders);
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
        }

    }
    private void viewOutstanding(){
        view.displayError("Not implemented yet");
    }
    private void viewFailed(){
        view.displayError("Not implemented yet");
    }
    private void manageRefunds(){
        //allows the merchant to go through the payments tagged for refund,
        // when they receive the return (or otherwise happy to issue refund)
        // they can confirm refund, and it'll refund to customer
        view.displayError("Not implemented yet");
    }


    private int getMenuChoice(){
        return view.displayMenuAndGetSelection();
    }

}
