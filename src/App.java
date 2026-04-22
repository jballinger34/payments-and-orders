import controller.PaymentController;
import dao.*;
import domain.model.PaymentMethod;
import domain.processor.CardPaymentProcessor;
import domain.processor.PaymentProcessor;
import exception.PersistenceException;
import gateway.FakePaymentGateway;
import gateway.PaymentGateway;
import service.AuditService;
import service.InventoryService;
import service.OrderService;
import service.PaymentService;
import view.PaymentView;
import view.UserIO;
import view.UserIOConsoleImpl;

import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        UserIO io = new UserIOConsoleImpl();
        PaymentView view = new PaymentView(io);

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
        // IN FUTURE SWAP OUT SO CONTROLLER TAKES ORDER SERVICE - THIS WILL BE OUR MAIN ORCHESTRATING SERVICE
        PaymentController controller = new PaymentController(view, paymentService);

        controller.run();


    }

}
