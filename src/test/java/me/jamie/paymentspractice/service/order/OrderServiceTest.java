package me.jamie.paymentspractice.service.order;

import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.data.request.CheckoutItemRequest;
import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import me.jamie.paymentspractice.service.PaymentService;
import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    InventoryService inventoryService;
    @Mock
    OrderDao orderDao;
    @Mock
    PaymentService paymentService;
    @Mock
    AuditService auditService;

    @InjectMocks
    OrderService orderService;



    //getAllOrders()
    @Test
    void testGetAllOrdersEmpty() throws PersistenceException {
        when(orderDao.findByMerchantId("TEST_MERCHANT")).thenReturn(new ArrayList<>());
        List<Order> orders = orderService.getAllOrders("TEST_MERCHANT");

        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    //placeOrder()
    @Test
    void testPlaceOrderFlow() throws Exception {
        Product product = new Product("PRODUCT_1","TEST_MERCHANT","PRODUCT_1",1.0,10);
        Payment payment = mock(Payment.class);
        when(inventoryService.getProduct("TEST_MERCHANT","PRODUCT_1")).thenReturn(product);
        when(inventoryService.isInStock("TEST_MERCHANT", "PRODUCT_1", 3)).thenReturn(true);

        when(paymentService.createPayment(anyDouble(),any())).thenReturn(payment);

        Order order = orderService.placeOrder("TEST_MERCHANT", List.of(new CheckoutItemRequest("PRODUCT_1", 3)), PaymentMethod.CARD);


        verify(paymentService).createPayment(anyDouble(),any());
        verify(orderDao).save(any());
        verify(auditService).logSuccess(AuditType.ORDER, AuditAction.CREATE, order.getId());

        assertNotNull(order);
        assertEquals(payment, order.getPayment());
    }

    @Test
    void testPlaceOrderInsufficientStock() throws Exception {
        Product product = new Product("PRODUCT_1","TEST_MERCHANT","PRODUCT_1",1.0,10);
        when(inventoryService.getProduct("TEST_MERCHANT", "PRODUCT_1")).thenReturn(product);
        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder("TEST_MERCHANT", List.of(new CheckoutItemRequest("PRODUCT_1", 100)), PaymentMethod.CARD));
    }

    @Test
    void testAuthPaymentSuccessFlow() throws Exception {
        Payment payment = mock(Payment.class);
        Order order = Order.fromPersistence("ORDER_1", "TEST_MERCHANT", payment, List.of(), OrderStatus.CREATED);

        when(payment.getStatus()).thenReturn(PaymentStatus.AUTHORIZED);

        orderService.authorizePayment(order);

        assertEquals(OrderStatus.READY, order.getStatus());
        verify(orderDao).save(any());
        verify(auditService).logSuccess(AuditType.ORDER, AuditAction.AUTH, order.getId());
    }

    @Test
    void testReserveStockFlow() throws Exception {
        Payment payment = mock(Payment.class);
        LineItem item = new LineItem(new Product("PRODUCT_1", "TEST_MERCHANT","PRODUCT_1", 1.0,10), 3);
        Order order = Order.fromPersistence("ORDER_1", "TEST_MERCHANT", payment, List.of(item), OrderStatus.READY);

        orderService.reserveStock(order);

        verify(inventoryService).reduceStock("TEST_MERCHANT", "PRODUCT_1",3);

        verify(orderDao).save(any());
        verify(auditService).logSuccess(AuditType.ORDER, AuditAction.REDUCE_STOCK, order.getId());
    }
    @Test
    void testReserveWithInsufficientStock() throws Exception {
        Payment payment = mock(Payment.class);
        LineItem item = new LineItem(new Product("PRODUCT_1", "TEST_MERCHANT","PRODUCT_1", 1.0,10), 30);
        Order order = Order.fromPersistence("ORDER_1", "TEST_MERCHANT", payment, List.of(item), OrderStatus.READY);

        when(inventoryService.reduceStock(any(), any(), anyInt())).thenThrow(new InsufficientStockException("Simulating not enough stock"));

        assertThrows(InsufficientStockException.class,() -> orderService.reserveStock(order)) ;

        verify(inventoryService).reduceStock("TEST_MERCHANT", "PRODUCT_1",30);
        verify(auditService).logFailure(eq(AuditType.ORDER), eq(AuditAction.REDUCE_STOCK), eq(order.getId()), anyString());
    }

    //capturePayment()
    @Test
    void testCapturePaymentFlow() throws Exception {
        /*

            verify(auditService).logSuccess(AuditType.ORDER, AuditAction.AUTH, order.getId());
         */
        Payment payment = mock(Payment.class);
        Order order = Order.fromPersistence("ORDER_1", "TEST_MERCHANT", payment, List.of(), OrderStatus.RESERVED);

        when(payment.getStatus()).thenReturn(PaymentStatus.CAPTURED);

        orderService.capturePayment(order);

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderDao).save(any());
        verify(auditService).logSuccess(any(), any(), any());
        assertEquals(PaymentStatus.CAPTURED, order.getPayment().getStatus());
    }



}