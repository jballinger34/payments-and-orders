import controller.PaymentController;
import dao.*;
import domain.model.PaymentMethod;
import domain.processor.CardPaymentProcessor;
import domain.processor.PaymentProcessor;
import exception.PersistenceException;
import gateway.FakePaymentGateway;
import gateway.PaymentGateway;
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

        PaymentDao dao;
        try {
            dao = new FilePaymentDao();
        } catch (PersistenceException e){
            view.displayError(e.getMessage());
            return;
        }
        AuditDao auditDao = new FileAuditDao();

        PaymentGateway gateway = new FakePaymentGateway();

        Map<PaymentMethod, PaymentProcessor> processors = new HashMap<>();
        processors.put(PaymentMethod.CARD, new CardPaymentProcessor(gateway));


        PaymentService paymentService = new PaymentService(dao,auditDao,processors);



        PaymentController controller = new PaymentController(view, paymentService);

        controller.run();


    }

}
