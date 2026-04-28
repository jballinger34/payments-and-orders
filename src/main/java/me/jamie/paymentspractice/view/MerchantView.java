package me.jamie.paymentspractice.view;

import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;

import java.util.List;

public class MerchantView {

    private UserIO io;

    public MerchantView(UserIO io){
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
    public void displayOrder(Order order){
        io.print("### Order ID: " + order.getId()+ " ###");
        io.print("--- Total: " + order.getTotal() + " ---");
        io.print("--- Status: " + order.getStatus() + " ---");
        displayPayment(order.getPayment());
        displayLineItems(order.getItems());
        io.print("########################");

    }
    public void displayAllOrders(List<Order> orders){
        io.print("=== Display All Orders ===");
        for(Order order : orders){
            displayOrder(order);
        }
        io.print("========================");
    }
    public void displayLineItem(LineItem item){
        io.print("------------------------");
        io.print("--- Product Id "+item.getProductId()+" ---");
        io.print("--- Product Name " + item.getProductName() + " ---");
        io.print("--- Quantity " + item.getQuantity() + " ---");
        io.print("--- Price " + item.getPriceAtPurchase() + " ---");
        io.print("------------------------");
    }
    public void displayLineItems(List<LineItem> items){
        io.print("=== Line Items ===");
        for(LineItem item : items){
            displayLineItem(item);
        }
        io.print("========================");
    }

    public void displayError(String message){
        io.print("ERROR: " + message);
    }

    public void setIo(UserIO io) {
        this.io = io;
    }

    public int displayMenuAndGetSelection() {
        io.print("=================");
        io.print("Merchant Menu");
        io.print("1. Manage Inventory");
        io.print("2. View All Orders");
        io.print("3. View Outstanding Orders");
        io.print("4. View Failed Orders");
        io.print("5. Handle Refunds");
        io.print("6. Exit");
        return io.readInt("Enter Menu Selection:",1,6);
    }
}
