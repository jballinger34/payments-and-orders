import controller.Controller;
import controller.CustomerController;
import controller.MainController;
import controller.MerchantController;
import dao.audit.AuditDao;
import dao.audit.FileAuditDao;
import dao.inventory.AlwaysInStockInventoryDao;
import dao.inventory.InventoryDao;
import dao.payment.FilePaymentDao;
import dao.payment.PaymentDao;
import domain.model.payment.PaymentMethod;
import domain.processor.CardPaymentProcessor;
import domain.processor.PaymentProcessor;
import exception.PersistenceException;
import gateway.FakePaymentGateway;
import gateway.PaymentGateway;
import service.audit.AuditService;
import service.InventoryService;
import service.OrderService;
import service.PaymentService;
import view.MainView;
import view.MerchantView;
import view.UserIO;
import view.UserIOConsoleImpl;

import java.util.*;

public class App {

    public static void main(String[] args) {
        UserIO io = new UserIOConsoleImpl();
        MerchantView view = new MerchantView(io);

        PaymentDao paymentDao;
        try {
            paymentDao = new FilePaymentDao();
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
            return;
        }
        AuditDao auditDao = new FileAuditDao();
        InventoryDao inventoryDao = new AlwaysInStockInventoryDao();

        PaymentGateway gateway = new FakePaymentGateway();

        Map<PaymentMethod, PaymentProcessor> processors = new HashMap<>();
        processors.put(PaymentMethod.CARD, new CardPaymentProcessor(gateway));

        AuditService auditService = new AuditService(auditDao);

        InventoryService inventoryService = new InventoryService(inventoryDao, auditService);
        PaymentService paymentService = new PaymentService(paymentDao,auditService,processors);

        OrderService orderService = new OrderService(paymentService,inventoryService,auditService);


        List<Controller> subControllers = Arrays.asList(new MerchantController(view, orderService), new CustomerController());


        MainView mainView = new MainView(io);
        MainController controller = new MainController(mainView, subControllers);


        controller.run();


    }

}
