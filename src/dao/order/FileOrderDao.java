package dao.order;

import dao.payment.PaymentDao;
import dao.product.ProductDao;
import domain.model.LineItem;
import domain.model.Product;
import domain.model.order.Order;
import domain.model.order.OrderStatus;
import domain.model.payment.Payment;
import exception.InvalidDataException;
import exception.OrderNotFoundException;
import exception.PersistenceException;

import java.io.*;
import java.util.*;

public class FileOrderDao implements OrderDao {

    private PaymentDao paymentDao;
    private ProductDao productDao;

    private Map<String, Order> orders = new HashMap<>();

    private final String ORDER_FILE = "orders.txt";
    private final String ORDER_FIELD_DELIMETER = "::";
    private final String LIST_DELIMETER = ";;";
    private final String ITEM_FIELD_DELIMETER = ",,";

    public FileOrderDao(PaymentDao paymentDao, ProductDao productDao) throws PersistenceException {
        this.paymentDao = paymentDao;
        this.productDao = productDao;

        loadOrders();

    }
    private void loadOrders() throws PersistenceException {
        Scanner scanner;
        File f = new File(ORDER_FILE);
        try{
            if(!f.exists()){
                f.createNewFile();
            }
            scanner = new Scanner(new FileReader(ORDER_FILE));
        } catch (IOException e){
            throw new PersistenceException("Error loading orders file.", e);
        }
        while (scanner.hasNextLine()){
            String currentLine = scanner.nextLine();
            String[] tokens = currentLine.split(ORDER_FIELD_DELIMETER);
            // entry format
            // Order: orderId::statusOrdinal::paymentId::items
            // Items: productIdStr,,quantity,,price;;productIdStr,,quantity,,price;;etc...
            if(tokens.length != 4){
                throw new InvalidDataException("Tried to load invalid order. Possible data corruption.");
            }
            try{
                String id = tokens[0];
                int statusOrdinal = Integer.parseInt(tokens[1]);
                String paymentId = tokens[2];
                Payment payment = paymentDao.findById(paymentId);
                String itemsToken = tokens[3];
                List<LineItem> items = new ArrayList<>();

                if(!itemsToken.isEmpty()){
                    String[] itemParts = itemsToken.split(LIST_DELIMETER);
                    for(String part : itemParts){
                        String[] fields = part.split(ITEM_FIELD_DELIMETER);

                        String productId = fields[0];
                        Product product = productDao.findById(productId);

                        int quantity = Integer.parseInt(fields[1]);
                        double price = Double.parseDouble(fields[2]);

                        LineItem item = new LineItem(product,quantity,price);
                        items.add(item);
                    }
                }
                Order order = Order.fromPersistence(id,items,payment, OrderStatus.values()[statusOrdinal]);
                orders.put(order.getId(), order);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e ){
                throw new InvalidDataException("Tried to load invalid payment. Possible data corruption.", e);
            }
        }
    }

    // tradeoff here between appending and adding new orders repeatedly (as we do in payment)
    // fast but file grows - keeps order history (redundant as we have audit)
    // or overwriting and rewriting all orders
    // slow but only has current order state
    // in a proper implementation db or otherwise I'd only overwrite the specific entry
    // but this is annoying to implement in a file
    private void writeAllOrders() throws PersistenceException {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(ORDER_FILE));
        } catch (IOException e){
            throw new PersistenceException("Could not save orders.", e );
        }
        for(Order order : orders.values()){
            out.println(getEntryString(order));
        }
        out.flush();
        out.close();
    }
    private String getEntryString(Order order){
        // entry format
        // Order: orderId::statusOrdinal::paymentId::items
        // Items: productIdStr,,quantity,,price;;productIdStr,,quantity,,price;;etc...
        StringBuilder sb = new StringBuilder()
                .append(order.getId()).append(ORDER_FIELD_DELIMETER)
                .append(order.getStatus().ordinal()).append(ORDER_FIELD_DELIMETER)
                .append(order.getPayment().getId()).append(ORDER_FIELD_DELIMETER);

        StringJoiner joiner = new StringJoiner(LIST_DELIMETER);
        for(LineItem item : order.getItems()){
            String entry = item.getProductId() + ITEM_FIELD_DELIMETER
                          + item.getQuantity() + ITEM_FIELD_DELIMETER
                          + item.getPriceAtPurchase();
            joiner.add(entry);
        }
        sb.append(joiner.toString());
        return sb.toString();

    }


    @Override
    public void save(Order order) throws PersistenceException {
        orders.put(order.getId(),order);
        writeAllOrders();
    }

    @Override
    public Order findById(String orderId) {
        Order order = orders.get(orderId);
        if(order == null) throw new OrderNotFoundException("No order with ID: " + orderId);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
}
