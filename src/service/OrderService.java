package service;

import domain.model.*;
import exception.InsufficientStockException;
import exception.PersistenceException;

import java.util.List;

public class OrderService {

    PaymentService paymentService;
    InventoryService inventorySerivce;
    AuditService auditService;

    public OrderService(PaymentService paymentService, InventoryService inventoryService, AuditService auditService){
        this.paymentService = paymentService;
        this.inventorySerivce = inventoryService;
        this.auditService = auditService;
    }

    //payment method may be swapped for payment details later. i dont handle any details at the moment for simplicity
    public Order placeOrder(List<LineItem> items, PaymentMethod paymentMethod) throws PersistenceException {
        // check stock
        for(LineItem item : items){
            boolean isInStock = inventorySerivce.isInStock(item.getProductId(), item.getQuantity());
            if(!isInStock){
                throw new InsufficientStockException("Not enough stock for product " + item.getProductId());
            }
        }
        //create order
        Order order = new Order(items);
        auditService.logOrderCreateAttempt(order.getId());

        //reduce stock
        for(LineItem item : items){
            inventorySerivce.reduceStock(item.getProductId(), item.getQuantity());
        }

        // create payment
        Payment payment = paymentService.createPayment(order.getTotal(), paymentMethod);
        order.setPayment(payment);

        return order;

    }
    public void processPayment(Order order) throws PersistenceException {
        if(order.getStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order not in CREATED state");
        }
        Payment payment = order.getPayment();
        paymentService.authorizePayment(payment);
        if(payment.getStatus() == PaymentStatus.AUTHORIZED){
            // audit log here -- change how audit works so not written
            order.markPaid();
        } else {
            order.cancel();
            // audit log here -- change how audit works so not written
        }
    }
    

}
