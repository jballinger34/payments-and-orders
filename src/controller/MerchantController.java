package controller;

import service.OrderService;
import view.MerchantView;

public class MerchantController implements Controller {

    private final MerchantView view;
    private final OrderService service;

    public MerchantController(MerchantView view, OrderService orderService) {
        this.view = view;
        this.service = orderService;
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

    @Override
    public String getName() {
        return "Merchant Mode";
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
