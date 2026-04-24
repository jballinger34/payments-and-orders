package service;

import dao.order.OrderDao;
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

    OrderDao dao;
    PaymentService paymentService;
    InventoryService inventoryService;
    AuditService auditService;

    public OrderService(OrderDao orderDao, PaymentService paymentService, InventoryService inventoryService, AuditService auditService){
        this.dao = orderDao;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.auditService = auditService;
    }

    public List<Order> getAllOrders() throws PersistenceException {
        return dao.findAll();
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

        dao.save(order);
        return order;

    }
    public void processPayment(Order order) throws PersistenceException {
        if(order.getStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order not in CREATED state");
        }
        Payment payment = order.getPayment();
        paymentService.authorizePayment(payment);

        // note that we dao.save in both branches
        // could extract to after the if-else statement
        // but i want to save BEFORE logging
        if(payment.getStatus() == PaymentStatus.AUTHORIZED){
            order.markPaid();
            dao.save(order);
            auditService.logSuccess(AuditType.ORDER, AuditAction.PROCESS_PAYMENT, order.getId());
        } else {
            order.cancel();
            dao.save(order);
            auditService.logFailure(AuditType.ORDER, AuditAction.PROCESS_PAYMENT, order.getId(), "PAYMENT_DECLINED");
        }

    }
    

}
