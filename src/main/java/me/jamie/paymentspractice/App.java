package me.jamie.paymentspractice;

import me.jamie.paymentspractice.controller.Controller;
import me.jamie.paymentspractice.controller.CustomerController;
import me.jamie.paymentspractice.controller.MainController;
import me.jamie.paymentspractice.controller.MerchantController;
import me.jamie.paymentspractice.dao.audit.AuditDao;
import me.jamie.paymentspractice.dao.audit.FileAuditDao;
import me.jamie.paymentspractice.dao.inventory.AlwaysInStockInventoryDao;
import me.jamie.paymentspractice.dao.inventory.FileInventoryDao;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.dao.order.FileOrderDao;
import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.dao.payment.FilePaymentDao;
import me.jamie.paymentspractice.dao.payment.PaymentDao;
import me.jamie.paymentspractice.dao.product.InMemoryProductDao;
import me.jamie.paymentspractice.dao.product.ProductDao;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.processor.CardPaymentProcessor;
import me.jamie.paymentspractice.domain.processor.PaymentProcessor;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.gateway.FakePaymentGateway;
import me.jamie.paymentspractice.gateway.PaymentGateway;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import me.jamie.paymentspractice.service.PaymentService;
import me.jamie.paymentspractice.service.ProductService;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.view.*;

import java.io.File;
import java.util.*;

public class App {

    static final String inventory_file = "inventory.txt";

    public static void main(String[] args) {
        //IO
        UserIO io = new UserIOConsoleImpl();

        //views
        MainView mainView = new MainView(io);
        MerchantView view = new MerchantView(io);
        CustomerView customerView = new CustomerView(io);

        //DAO
        ProductDao productDao = new InMemoryProductDao();
        PaymentDao paymentDao;
        OrderDao orderDao;
        InventoryDao inventoryDao;
        try {
            paymentDao = new FilePaymentDao();
            orderDao = new FileOrderDao(paymentDao, productDao);
            inventoryDao = new FileInventoryDao(inventory_file);
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
            return;
        }
        AuditDao auditDao = new FileAuditDao();

        // Payment specific - gateway + processors
        PaymentGateway gateway = new FakePaymentGateway();

        Map<PaymentMethod, PaymentProcessor> processors = new HashMap<>();
        processors.put(PaymentMethod.CARD, new CardPaymentProcessor(gateway));

        //services

        AuditService auditService = new AuditService(auditDao);
        ProductService productService = new ProductService(productDao);
        // InventoryService - NEED TO ADD SERVICE METHODS TO CREATE NEW PRODUCTS, AND SET AMOUNT OF STOCK
        InventoryService inventoryService = new InventoryService(inventoryDao, auditService);
        PaymentService paymentService = new PaymentService(paymentDao,auditService,processors);

        OrderService orderService = new OrderService(orderDao,paymentService,inventoryService,auditService);


        List<Controller> subControllers = Arrays.asList(
                new MerchantController(view, orderService),
                new CustomerController(productService,inventoryService,orderService,customerView)
        );

        MainController controller = new MainController(mainView, subControllers);


        controller.run();


    }

}
