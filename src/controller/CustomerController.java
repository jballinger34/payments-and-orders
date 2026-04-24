package controller;

import domain.model.LineItem;
import domain.model.Product;
import domain.model.order.Order;
import domain.model.order.OrderStatus;
import domain.model.payment.PaymentMethod;
import exception.InsufficientStockException;
import exception.PersistenceException;
import exception.ProductNotFoundException;
import service.InventoryService;
import service.OrderService;
import service.ProductService;
import view.CustomerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerController implements Controller {

    ProductService productService;
    InventoryService inventoryService;
    OrderService orderService;
    CustomerView view;

    private final List<LineItem> cart = new ArrayList<>();

    public CustomerController(ProductService productService, InventoryService inventoryService, OrderService orderService, CustomerView customerView){
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.productService = productService;
        this.view = customerView;
    }

    public void run(){
        boolean running = true;
        while(running){
            int selection = view.displayMenuAndGetSelection();
            switch (selection){
                case 1:
                    browse();
                    break;
                case 2:
                    viewCart();
                    break;
                case 3:
                    checkout();
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public String getName() {
        return "Customer Mode";
    }

    private void browse() {
        while(true){
            //get product that user wants to buy
            Product product = getProduct();
            if(product == null){
                //user wants to stop browsing
                break;
            }
            //check in stock
            try{
                int amtInStock = inventoryService.getStockQty(product.getId());
                // get qty of products user wants to buy
                int quantity = getQuantity(amtInStock);
                if(amtInStock < quantity){
                    // this shouldnt ever happen - the user should only be
                    // able to pick amount from 1-amtInStock
                    view.displayMessage("Only " + amtInStock + " of " + product.getName() + " in stock.");
                    view.displayMessage("Adding " + amtInStock + " to cart.");
                    quantity = amtInStock;
                }

                // HERE IS A GOOD CHANCE TO APPLY DISCOUNT
                // CURRENTLY WE USE LineItem that gets priceAtPurchase as product*quantity
                // in future could use some builder that applies discounts.
                LineItem item = new LineItem(product,quantity);
                addToCart(item);

            } catch (ProductNotFoundException | PersistenceException e){
                view.displayError(e.getMessage());
            }

        }
    }

    private void addToCart(LineItem item) {
        cart.add(item);
    }

    private void viewCart(){
        view.displayCart(cart);
    }
    //TODO
    // MAY NEED TO BREAK CHECKOUT UP INTO SMALLER METHODS
    // GOOD FOR NOW THOUGH AS AN INITIAL IMPL
    private void checkout(){
        if(cart.isEmpty()){
            view.displayError("Cart is empty");
            return;
        }
        try{
            Order order = orderService.placeOrder(new ArrayList<>(cart), PaymentMethod.CARD);
            view.displayMessage("Order created!");

            // get auth, try move CREATED -> READY
            orderService.authorizePayment(order);
            if(order.getStatus() != OrderStatus.READY) {
                view.displayError("Payment failed. Order cancelled.");
                return;
            }
            view.displayMessage("Payment authorized!");

            // reserve stock, READY -> RESERVED
            orderService.reserveStock(order);
            if(order.getStatus() != OrderStatus.RESERVED){
                view.displayError("Reserving Stock failed. Order cancelled.");
                return;
            }
            view.displayMessage("Stock reserved!");

            // automatic capture at the moment
            // capture payment, RESERVED -> PAID
            orderService.capturePayment(order);
            if(order.getStatus() != OrderStatus.PAID){
                view.displayError("Failed to capture payment. Order cancelled.");
                return;
            }
            view.displayMessage("Payment captured");


            view.displayOrder(order);
            cart.clear();

        } catch (PersistenceException | InsufficientStockException e){
            view.displayError("Something went wrong: " + e.getMessage());
            view.displayError("Order cancelled.");
        }
    }


    private Product getProduct(){
        return view.displayProductsAndGetSelection(productService.getAllProducts());
    }
    private int getQuantity(int amtInStock){
        return view.getQuantity(amtInStock);
    }
}
