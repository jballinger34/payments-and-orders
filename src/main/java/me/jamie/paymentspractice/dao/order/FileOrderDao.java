package me.jamie.paymentspractice.dao.order;

import jakarta.annotation.PostConstruct;
import me.jamie.paymentspractice.dao.inventory.InventoryDao;
import me.jamie.paymentspractice.dao.payment.PaymentDao;
import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.dto.LineItemRecord;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.InvalidDataException;
import me.jamie.paymentspractice.exception.OrderNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileOrderDao implements OrderDao {

    private Map<String, OrderRecord> orders = new HashMap<>();

    private final String ORDER_FILE = "orders.txt";
    private final String ORDER_FIELD_DELIMETER = "::";
    private final String LIST_DELIMETER = ";;";
    private final String ITEM_FIELD_DELIMETER = ",,";

    @PostConstruct
    public void init() throws PersistenceException{
        loadOrderRecords();
    }


    private void loadOrderRecords() throws PersistenceException {
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

                List<LineItemRecord> items = new ArrayList<>();
                String itemsToken = tokens[3];

                if(!itemsToken.isEmpty()){
                    String[] itemParts = itemsToken.split(LIST_DELIMETER);
                    for(String part : itemParts){
                        String[] fields = part.split(ITEM_FIELD_DELIMETER);
                        if(fields.length != 3){
                            throw new InvalidDataException("Invalid line item");
                        }
                        items.add(new LineItemRecord(fields[0],
                                Integer.parseInt(fields[1]),
                                Double.parseDouble(fields[2])
                        ));

                    }
                }
                orders.put(id, new OrderRecord(id,statusOrdinal,paymentId,items));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e ){
                throw new InvalidDataException("Tried to load invalid order. Possible data corruption.", e);
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
        for(OrderRecord order : orders.values()){
            out.println(getEntryString(order));
        }
        out.flush();
        out.close();
    }
    private String getEntryString(OrderRecord order){
        // entry format
        // Order: orderId::statusOrdinal::paymentId::items
        // Items: productIdStr,,quantity,,price;;productIdStr,,quantity,,price;;etc...
        StringBuilder sb = new StringBuilder()
                .append(order.id()).append(ORDER_FIELD_DELIMETER)
                .append(order.statusOrdinal()).append(ORDER_FIELD_DELIMETER)
                .append(order.paymentId()).append(ORDER_FIELD_DELIMETER);

        StringJoiner joiner = new StringJoiner(LIST_DELIMETER);
        for(LineItemRecord item : order.items()){
            String entry = item.productId() + ITEM_FIELD_DELIMETER
                          + item.quantity() + ITEM_FIELD_DELIMETER
                          + item.price();
            joiner.add(entry);
        }
        sb.append(joiner.toString());
        return sb.toString();

    }


    @Override
    public void save(OrderRecord order) throws PersistenceException {
        orders.put(order.id(), order);
        writeAllOrders();
    }

    @Override
    public OrderRecord findById(String orderId) {
        OrderRecord order = orders.get(orderId);
        if(order == null) throw new OrderNotFoundException("No order with ID: " + orderId);
        return order;
    }

    @Override
    public List<OrderRecord> findAll() {
        return new ArrayList<>(orders.values());
    }
}
