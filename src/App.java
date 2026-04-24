import controller.Controller;
import controller.CustomerController;
import controller.MainController;
import controller.MerchantController;
import dao.audit.AuditDao;
import dao.audit.FileAuditDao;
import dao.inventory.AlwaysInStockInventoryDao;
import dao.inventory.InventoryDao;
import dao.order.FileOrderDao;
import dao.order.InMemoryOrderDao;
import dao.order.OrderDao;
import dao.payment.FilePaymentDao;
import dao.payment.PaymentDao;
import dao.product.InMemoryProductDao;
import dao.product.ProductDao;
import domain.model.Product;
import domain.model.payment.PaymentMethod;
import domain.processor.CardPaymentProcessor;
import domain.processor.PaymentProcessor;
import exception.PersistenceException;
import gateway.FakePaymentGateway;
import gateway.PaymentGateway;
import service.ProductService;
import service.audit.AuditService;
import service.InventoryService;
import service.OrderService;
import service.PaymentService;
import view.*;

import java.util.*;

public class App {

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
        try {
            paymentDao = new FilePaymentDao();
            orderDao = new FileOrderDao(paymentDao, productDao);
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
            return;
        }
        AuditDao auditDao = new FileAuditDao();
        InventoryDao inventoryDao = new AlwaysInStockInventoryDao();

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
