package controller;

import domain.model.order.Order;
import exception.PersistenceException;
import service.OrderService;
import view.MerchantView;

import java.util.List;

public class MerchantController implements Controller {

    private final MerchantView view;
    private final OrderService orderService;

    public MerchantController(MerchantView view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    public void run() {
        //uncomment to add sample data on run
        //addSampleData();
        
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

    private void addSampleData(){
        try {
            //createSampleOrder, like this method is temporary to hard code some sample data to work with

            //TODO
            // customer controller gets view to take user IO
            // then controller tells Inventory or Product service
            // to build LineItems with the users input
            // this interacts with the inventory and product daos
            // to create List<LineItem> to THEN use orderService.createOrder
            Order o1 = orderService.createSampleOrder();
            orderService.processPayment(o1);
            Order o2 = orderService.createSampleOrder();


        } catch(PersistenceException e){
            view.displayError(e.getMessage());
        }
    }
}
