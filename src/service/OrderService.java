package service;

import domain.model.*;
import domain.model.order.Order;
import domain.model.order.OrderStatus;
import domain.model.payment.Payment;
import domain.model.payment.PaymentMethod;
import domain.model.payment.PaymentStatus;
import exception.InsufficientStockException;
import exception.PersistenceException;
import service.audit.AuditAction;
import service.audit.AuditService;
import service.audit.AuditType;

import java.util.List;

public class OrderService {

    PaymentService paymentService;
    InventoryService inventoryService;
    AuditService auditService;

    public OrderService(PaymentService paymentService, InventoryService inventoryService, AuditService auditService){
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.auditService = auditService;
    }

    //payment method may be swapped for payment details later. i dont handle any details at the moment for simplicity
    public Order placeOrder(List<LineItem> items, PaymentMethod paymentMethod) throws PersistenceException {
        // check stock
        for(LineItem item : items){
            boolean isInStock = inventoryService.isInStock(item.getProductId(), item.getQuantity());
            if(!isInStock){
                throw new InsufficientStockException("Not enough stock for product " + item.getProductId());
            }
        }
        //create order
        Order order = new Order(items);
        auditService.logAttempt(AuditType.ORDER, AuditAction.CREATE, order.getId());

        //reduce stock
        for(LineItem item : items){
            inventoryService.reduceStock(item.getProductId(), item.getQuantity());
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
            auditService.logSuccess(AuditType.ORDER, AuditAction.PROCESS_PAYMENT, order.getId());
            order.markPaid();
        } else {
            order.cancel();
            auditService.logFailure(AuditType.ORDER, AuditAction.PROCESS_PAYMENT, order.getId(), "PAYMENT_DECLINED");
        }
    }
    

}
