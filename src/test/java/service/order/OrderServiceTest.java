package service.order;

import stubs.OrderDaoStubImpl;
import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import me.jamie.paymentspractice.service.PaymentService;
import me.jamie.paymentspractice.service.audit.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import stubs.AuditServiceStub;
import stubs.InventoryServiceStub;
import stubs.PaymentServiceStub;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    OrderService orderService;
    InventoryService inventoryService;
    private static List<LineItem> ITEM_10_IN_STOCK;
    private static List<LineItem> ITEM_NONE_IN_STOCK;


    @BeforeEach
    void setUp() throws PersistenceException {

        OrderDao orderDao = new OrderDaoStubImpl();

        PaymentService paymentService = new PaymentServiceStub();
        inventoryService = new InventoryServiceStub();
        AuditService auditService = new AuditServiceStub();

        orderService = new OrderService(orderDao,paymentService,inventoryService,auditService);

        ITEM_10_IN_STOCK = List.of(new LineItem(inventoryService.getProduct("PRODUCT_1"), 1,1));
        ITEM_NONE_IN_STOCK = List.of(new LineItem(inventoryService.getProduct("PRODUCT_2"), 1, 1));
    }

    //getAllOrders()
    @Test
    void getAllOrders_emptyInitially() throws PersistenceException {
        List<Order> orders = orderService.getAllOrders();
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    void getAllOrders_afterPlacingOrder() throws PersistenceException {

        orderService.placeOrder(ITEM_10_IN_STOCK, PaymentMethod.CARD);

        List<Order> orders = orderService.getAllOrders();
        assertEquals(1, orders.size());
    }

    //placeOrder()
    @Test
    void placeOrder_success() throws Exception {
        Order order = orderService.placeOrder(ITEM_10_IN_STOCK, PaymentMethod.CARD);

        assertNotNull(order);
        assertNotNull(order.getPayment());
        assertEquals(1, orderService.getAllOrders().size());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(PaymentStatus.PENDING, order.getPayment().getStatus());
    }

    @Test
    void placeOrder_insufficientStock() {
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(ITEM_NONE_IN_STOCK, PaymentMethod.CARD));
    }

    //authorizePayment()
    @Test
    void authorizePayment_success() throws Exception {
        Order order = orderService.placeOrder(ITEM_10_IN_STOCK, PaymentMethod.CARD);

        orderService.authorizePayment(order);

        assertEquals(OrderStatus.READY, order.getStatus());
        assertEquals(PaymentStatus.AUTHORIZED, order.getPayment().getStatus());
    }

    //reserveStock()
    @Test
    void reserveStock_success() throws Exception {
        int stockAmt = inventoryService.getStockQty("PRODUCT_1");
        Order order = orderService.placeOrder(ITEM_10_IN_STOCK, PaymentMethod.CARD);

        orderService.authorizePayment(order);
        orderService.reserveStock(order);

        assertEquals(OrderStatus.RESERVED, order.getStatus());
        assertEquals(PaymentStatus.AUTHORIZED, order.getPayment().getStatus());

        //initial stock amount - amount in order should equal amount now in inventory
        assertEquals(stockAmt - 1, inventoryService.getStockQty("PRODUCT_1"));
    }

    //capturePayment()
    @Test
    void capturePayment_success() throws Exception {
        Order order = orderService.placeOrder(ITEM_10_IN_STOCK, PaymentMethod.CARD);

        orderService.authorizePayment(order);
        orderService.reserveStock(order);
        orderService.capturePayment(order);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(PaymentStatus.CAPTURED, order.getPayment().getStatus());
    }



}