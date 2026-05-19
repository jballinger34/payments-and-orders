package me.jamie.paymentspractice.service;


import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.data.entity.LineItemEntity;
import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.Merchant;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.data.request.CheckoutItemRequest;
import me.jamie.paymentspractice.data.dto.OrderDto;
import me.jamie.paymentspractice.data.entity.OrderEntity;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.OrderNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.audit.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    OrderDao dao;
    PaymentService paymentService;
    InventoryService inventoryService;
    MerchantService merchantService;
    AuditService auditService;


    public OrderService(OrderDao orderDao, PaymentService paymentService, InventoryService inventoryService, AuditService auditService){
        this.dao = orderDao;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.auditService = auditService;
    }
    public OrderDto getOrder(String merchantId, String orderId) throws PersistenceException, OrderNotFoundException {
        return OrderDto.from(buildOrder(dao.findByMerchantIdAndId(merchantId,orderId)));
    }

    //needs to be changed to return List of OrderDtos
    public List<Order> getAllOrders(String merchantId) throws PersistenceException {
        List<OrderEntity> records = dao.findByMerchantId(merchantId);
        List<Order> orders = new ArrayList<>();
        for(OrderEntity record : records){
            orders.add(buildOrder(record));
        }
        return orders;
    }


    public Order placeOrder(String merchantId, List<CheckoutItemRequest> cart, PaymentMethod paymentMethod) throws PersistenceException, InsufficientStockException {
        if(cart.isEmpty()) throw new IllegalArgumentException("Empty cart");


        List<LineItem> items = new ArrayList<>();
        for(CheckoutItemRequest item : cart){
            Product p = inventoryService.getProduct(merchantId,item.productId());
            items.add(new LineItem(p,item.quantity()));
        }


        // check stock
        for(LineItem item : items){
            boolean isInStock = inventoryService.isInStock(merchantId, item.getProductId(), item.getQuantity());
            if(!isInStock){
                throw new InsufficientStockException("Not enough stock for product " + item.getProductId());
            }
        }
        //create order
        Order order = new Order(merchantId, items);
        try {
            auditService.logAttempt(AuditType.ORDER, AuditAction.CREATE, order.getId());

            // create payment
            Payment payment = paymentService.createPayment(order.getTotal(), paymentMethod);
            order.setPayment(payment);
            dao.save(toEntity(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.CREATE, order.getId());
            return order;
        } catch (PersistenceException | InsufficientStockException e) {
            order.cancel();
            dao.save(toEntity(order));
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
                dao.save(toEntity(order));
                auditService.logSuccess(AuditType.ORDER, AuditAction.AUTH, order.getId());
            } else {
                order.cancel();
                dao.save(toEntity(order));
                auditService.logFailure(AuditType.ORDER, AuditAction.AUTH, order.getId(), "PAYMENT_DECLINED");
            }
        } catch (PersistenceException e){
            order.cancel();
            dao.save(toEntity(order));
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
                inventoryService.reduceStock(order.getMerchantId(), item.getProductId(), item.getQuantity());
            }
            order.markReserved();
            dao.save(toEntity(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.REDUCE_STOCK, order.getId());

        } catch (InsufficientStockException | PersistenceException e){
            order.cancel();
            dao.save(toEntity(order));
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
                dao.save(toEntity(order));
                auditService.logSuccess(AuditType.ORDER, AuditAction.CAPTURE, order.getId());
            } else {
                order.cancel();
                dao.save(toEntity(order));
                auditService.logFailure(AuditType.ORDER, AuditAction.CAPTURE, order.getId(), "NO_REASON_YET");
            }

        } catch (PersistenceException e){
            order.cancel();
            dao.save(toEntity(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.CAPTURE, order.getId(), e.getMessage());
            throw e;
        }
    }

    public OrderDto fulfil(String merchantId, String orderId) throws PersistenceException, OrderNotFoundException {
        Order order = buildOrder(dao.findByMerchantIdAndId(merchantId, orderId));
        if(order.getStatus() != OrderStatus.PAID){
            throw new IllegalStateException("Order not in PAID state");
        }
        try {
            auditService.logAttempt(AuditType.ORDER, AuditAction.FULFIL, orderId);
            //at the moment this method is just marking fulfilled or not, so no reason to fail (apart from persistence issue)
            //any additional business logic in fulfilling order will go here
            // then we will need to split off into if success, else (like other methods)
            order.markFulfilled();
            dao.save(toEntity(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.FULFIL, orderId);

        } catch (PersistenceException e){
            order.cancel();
            dao.save(toEntity(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.FULFIL, orderId, e.getMessage());
        }
        return OrderDto.from(order);
    }

    public OrderDto complete(String merchantId, String orderId) throws PersistenceException, OrderNotFoundException {
        Order order = buildOrder(dao.findByMerchantIdAndId(merchantId, orderId) );
        if(order.getStatus() != OrderStatus.FULFILLED){
            throw new IllegalStateException("Order not in FULFILLED state");
        }
        try {
            auditService.logAttempt(AuditType.ORDER, AuditAction.COMPLETE, orderId);
            //could for example check if this has been fulfilled more than 10 days ago
            // if not, then it is too soon to be completed, refunds can still be requested
            // cant complete order

            order.markCompleted();
            dao.save(toEntity(order));
            auditService.logSuccess(AuditType.ORDER, AuditAction.COMPLETE, orderId);

        } catch (PersistenceException e){
            order.cancel();
            dao.save(toEntity(order));
            auditService.logFailure(AuditType.ORDER, AuditAction.COMPLETE, orderId, e.getMessage());
        }
        return OrderDto.from(order);
    }

    private Order buildOrder(OrderEntity entity) throws PersistenceException {
        Payment payment = paymentService.getPayment(entity.getPaymentId());
        List<LineItem> items = new ArrayList<>();
        for(LineItemEntity itemEntity : entity.getItems()){
            Product product = inventoryService.getProduct(entity.getMerchantId(), itemEntity.getProductId());
            LineItem item = new LineItem(product,itemEntity.getQuantity(), itemEntity.getPrice());
            items.add(item);
        }
        OrderStatus status = entity.getStatus();

        return new Order(entity.getId(), entity.getMerchantId(), payment, items, status);
    }
    private OrderEntity toEntity(Order order){

        OrderEntity entity = new OrderEntity(order.getId(), order.getMerchantId(), order.getStatus(), order.getPayment().getId());

        List<LineItemEntity> items = new ArrayList<>();
        for (LineItem item : order.getItems()) {
            items.add(new LineItemEntity(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPriceAtPurchase()
            ));
        }

        entity.setItems(items);

        return entity;
    }

}
