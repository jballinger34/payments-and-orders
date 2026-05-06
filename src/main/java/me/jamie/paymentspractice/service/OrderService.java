package me.jamie.paymentspractice.service;


import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.dto.LineItemRecord;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        List<OrderRecord> records = dao.findAll();
        List<Order> orders = new ArrayList<>();
        for(OrderRecord record : records){
            orders.add(buildOrder(record));
        }
        return orders;
    }


    //payment method may be swapped for payment details later. i dont handle any details at the moment for simplicity
    public Order placeOrder(List<LineItem> items, PaymentMethod paymentMethod) throws PersistenceException, InsufficientStockException {
        // check stock
        for(LineItem item : items){
            boolean isInStock = inventoryService.isInStock(item.getProductId(), item.getQuantity());
            if(!isInStock){
                throw new InsufficientStockException("Not enough stock for product " + item.getProductId());
            }
        }
        //create order
        Order order = new Order(items);
        try {
            auditService.logAttempt(AuditType.ORDER, AuditAction.CREATE, order.getId());

            // create payment
            Payment payment = paymentService.createPayment(order.getTotal(), paymentMethod);
            order.setPayment(payment);
            dao.save(toRecord(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.CREATE, order.getId());
            return order;
        } catch (PersistenceException | InsufficientStockException e) {
            order.cancel();
            dao.save(toRecord(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.CREATE, order.getId(), e.getMessage());

            //we throw e here (and in other methods to do with order flow)
            //so that the controller can then use the view to display the error
            throw e;
        }



    }
    public void authorizePayment(Order order) throws PersistenceException {
        if(order.getStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order not in CREATED state");
        }
        try {
            auditService.logAttempt(AuditType.ORDER, AuditAction.AUTH, order.getId());
            Payment payment = order.getPayment();
            paymentService.authorizePayment(payment);

            if(payment.getStatus() == PaymentStatus.AUTHORIZED){
                order.markReady();
                dao.save(toRecord(order));
                auditService.logSuccess(AuditType.ORDER, AuditAction.AUTH, order.getId());
            } else {
                order.cancel();
                dao.save(toRecord(order));
                auditService.logFailure(AuditType.ORDER, AuditAction.AUTH, order.getId(), "PAYMENT_DECLINED");
            }
        } catch (PersistenceException e){
            order.cancel();
            dao.save(toRecord(order));
            auditService.logFailure(AuditType.ORDER,AuditAction.AUTH, order.getId(), e.getMessage());
            throw e;
        }

    }
    public void reserveStock(Order order) throws PersistenceException {
        if(order.getStatus() != OrderStatus.READY){
            throw new IllegalStateException("Order not in READY state");
        }

        try {

            auditService.logAttempt(AuditType.ORDER, AuditAction.REDUCE_STOCK, order.getId());

            List<LineItem> items = order.getItems();
            for(LineItem item : items){
                inventoryService.reduceStock(item.getProductId(), item.getQuantity());
            }
            order.markReserved();
            dao.save(toRecord(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.REDUCE_STOCK, order.getId());

        } catch (PersistenceException e){
            order.cancel();
            dao.save(toRecord(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.REDUCE_STOCK, order.getId(), e.getMessage());
            throw e;
        }


    }

    public void capturePayment(Order order) throws PersistenceException {
        if(order.getStatus() != OrderStatus.RESERVED){
            throw new IllegalStateException("Order not in RESERVED state");
        }
        try{
            auditService.logAttempt(AuditType.ORDER, AuditAction.CAPTURE, order.getId());
            Payment payment = order.getPayment();
            paymentService.capturePayment(payment);
            if(payment.getStatus() == PaymentStatus.CAPTURED){
                order.markPaid();
                dao.save(toRecord(order));
                auditService.logSuccess(AuditType.ORDER, AuditAction.CAPTURE, order.getId());
            } else {
                order.cancel();
                dao.save(toRecord(order));
                auditService.logFailure(AuditType.ORDER, AuditAction.CAPTURE, order.getId(), "NO_REASON_YET");
            }

        } catch (PersistenceException e){
            order.cancel();
            dao.save(toRecord(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.CAPTURE, order.getId(), e.getMessage());
            throw e;
        }
    }

    private Order buildOrder(OrderRecord record) throws PersistenceException {
        Payment payment = paymentService.getPayment(record.paymentId());
        List<LineItem> items = new ArrayList<>();
        for(LineItemRecord itemRecord : record.items()){
            Product product = inventoryService.getProduct(itemRecord.productId());
            LineItem item = new LineItem(product,itemRecord.quantity(),itemRecord.price());
            items.add(item);
        }
        OrderStatus status = OrderStatus.values()[record.statusOrdinal()];

        return Order.fromPersistence(record.id(), items, payment, status);
    }
    private OrderRecord toRecord(Order order){
        List<LineItemRecord> items = new ArrayList<>();

        for (LineItem item : order.getItems()) {
            items.add(new LineItemRecord(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPriceAtPurchase()
            ));
        }

        return new OrderRecord(order.getId(), order.getStatus().ordinal(), order.getPayment().getId(), items);
    }

}
