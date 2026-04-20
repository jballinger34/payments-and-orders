import controller.PaymentController;
import dao.FilePaymentDao;
import dao.InMemoryPaymentDao;
import dao.PaymentDao;
import domain.model.PaymentMethod;
import domain.processor.CardPaymentProcessor;
import domain.processor.PaymentProcessor;
import gateway.FakePaymentGateway;
import gateway.PaymentGateway;
import service.PaymentService;
import view.PaymentView;
import view.UserIO;
import view.UserIOConsoleImpl;

import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args){
        PaymentDao dao = new FilePaymentDao();
        PaymentGateway gateway = new FakePaymentGateway();

        Map<PaymentMethod, PaymentProcessor> processors = new HashMap<>();
        processors.put(PaymentMethod.CARD, new CardPaymentProcessor(gateway));


        PaymentService paymentService = new PaymentService(dao,processors);

        UserIO io = new UserIOConsoleImpl();
        PaymentView view = new PaymentView(io);

        PaymentController controller = new PaymentController(view, paymentService);

        controller.run();


    }

}
