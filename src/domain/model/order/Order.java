package domain.model.order;

import domain.model.LineItem;
import domain.model.payment.Payment;

import java.util.List;
import java.util.UUID;

public class Order {

    private final String id;


    private final List<LineItem> items;

    private Payment payment;
    private OrderStatus status;

    public Order(List<LineItem> items){
        this.id = UUID.randomUUID().toString();
        this.items = items;

        this.status = OrderStatus.CREATED;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    public double getTotal(){
        double total = 0;
        for(LineItem item : items){
            total += item.getPriceAtPurchase() * item.getQuantity();
        }
        return total;
    }
    public void markPaid(){
        if(status != OrderStatus.CREATED){
            throw new IllegalStateException("Only CREATED orders can be marked as paid ");
        }
        status = OrderStatus.PAID;
    }
    public void cancel() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a COMPLETED order");
        }
        status = OrderStatus.CANCELLED;
    }



    public String getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Payment getPayment() {
        return payment;
    }


}
