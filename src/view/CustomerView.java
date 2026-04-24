package view;

import domain.model.LineItem;
import domain.model.Product;
import domain.model.order.Order;
import domain.model.payment.Payment;
import domain.model.payment.PaymentStatus;
import service.ProductService;

import javax.sound.sampled.Line;
import java.util.Iterator;
import java.util.List;

public class CustomerView {

    //lots of methods in the different views overlap - need to sort

    private UserIO io;

    public CustomerView(UserIO io){
        this.io = io;
    }

    public int displayMenuAndGetSelection() {
        io.print("=================");
        io.print("Customer Menu");
        io.print("1. Browse");
        io.print("2. View Cart");
        io.print("3. Checkout");
        io.print("4. Exit");;
        return io.readInt("Enter Menu Selection:",1,4);
    }
    public void displayCart(List<LineItem> cart){
        io.print("=== Cart ===");
        for(LineItem lineItem : cart){
            displayLineItem(lineItem);
        }
        io.print("========================");
    }
    public void displayOrder(Order order){
        io.print("### Order ID: " + order.getId()+ " ###");
        io.print("--- Total: " + order.getTotal() + " ---");
        io.print("--- Status: " + order.getStatus() + " ---");
        displayPayment(order.getPayment());
        displayCart(order.getItems());
        io.print("########################");
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
    public void displayLineItem(LineItem item){
        io.print("------------------------");
        io.print("--- Product Id "+item.getProductId()+" ---");
        io.print("--- Product Name " + item.getProductName() + " ---");
        io.print("--- Quantity " + item.getQuantity() + " ---");
        io.print("--- Price " + item.getPriceAtPurchase() + " ---");
        io.print("------------------------");
    }

    public void displayError(String message){
        io.print("ERROR: " + message);
    }

    public void confirm() {
        io.readString("Press enter to confirm.");
    }

    public void displayMessage(String s) {
        io.print(s);
    }
    public Product displayProductsAndGetSelection(List<Product> products){
        io.print("=== All Products ===");
        for(int x = 0; x < products.size(); x++){
            io.print("--- Product " + (x+1) + ": ---");
            io.print("--- " + products.get(x).getName() + " ---");
            io.print("--- £ " + products.get(x).getCost() + " ---");
        }
        int exit = products.size()+1;
        int choice = io.readInt("Please choose a product or " + exit + " to stop browsing.",1,products.size() + 1);
        int index = choice - 1; // move down so in line with indexes
        if(index == products.size()) return null; // exit picked
        return products.get(index);
    }
    public int getQuantity(int amtInStock) {
        return io.readInt("Quantity: ",1,amtInStock);
    }

    public void setIo(UserIO io) {
        this.io = io;
    }


}
