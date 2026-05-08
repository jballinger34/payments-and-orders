package me.jamie.paymentspractice.domain.model.order;

import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.payment.Payment;

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
    private Order(String id, List<LineItem> items){
        this.id = id;
        this.items = items;
    }

    public static Order fromPersistence(String id, List<LineItem> items, Payment payment, OrderStatus status) {
        Order order = new Order(id, items);
        order.payment = payment;
        order.status = status;
        return order;
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
    public void markReady(){
        if(status != OrderStatus.CREATED){
            throw new IllegalStateException("Only CREATED orders can be marked as ready ");
        }
        status = OrderStatus.READY;
    }
    public void markReserved() {
        if(status != OrderStatus.READY){
            throw new IllegalStateException("Only READY orders can be marked as reserved ");
        }
        status = OrderStatus.RESERVED;
    }
    public void markPaid(){
        if(status != OrderStatus.RESERVED){
            throw new IllegalStateException("Only RESERVED orders can be marked as paid ");
        }
        status = OrderStatus.PAID;
    }
    public void markFulfilled(){
        if(status != OrderStatus.PAID){
            throw new IllegalStateException("Only PAID orders can be marked as fulfilled ");
        }
        status = OrderStatus.FULFILLED;
    }
    public void markCompleted(){
        if(status != OrderStatus.FULFILLED){
            throw new IllegalStateException("Only FULFILLED orders can be marked as completed");
        }
        status = OrderStatus.COMPLETED;
    }


    public void cancel() {
        if(status == OrderStatus.CREATED || status == OrderStatus.READY || status == OrderStatus.RESERVED){
            status = OrderStatus.CANCELLED;
        } else if (status == OrderStatus.PAID || status == OrderStatus.FULFILLED) {
            status = OrderStatus.REFUND_REQUESTED;
        } else {
            throw new IllegalStateException("Cannot cancel an order in state " + status);
        }

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

    public List<LineItem> getItems() {
        return items;
    }


}
