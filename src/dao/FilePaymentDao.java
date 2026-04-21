package dao;

import domain.model.AuthFailureReason;
import domain.model.Payment;
import domain.model.PaymentMethod;
import domain.model.PaymentStatus;
import exception.InvalidDataException;
import exception.PersistenceException;

import java.io.*;
import java.util.*;

public class FilePaymentDao implements PaymentDao {

    private Map<String, Payment> payments = new HashMap<>();

    private static final String DELIMITER = "::";
    private static final String PAYMENTS_FILE = "payments.txt";

    public FilePaymentDao() throws PersistenceException {
        loadPayments();
    }


    @Override
    public void save(Payment payment) throws PersistenceException {
        payments.put(payment.getId(), payment);
        writePayment(payment);
    }

    @Override
    public Payment findById(String id){
        return payments.get(id);
    }

    @Override
    public List<Payment> findAll(){
        return new ArrayList<>(payments.values());
    }

    private void loadPayments() throws PersistenceException {
        Scanner scanner;
        File f = new File(PAYMENTS_FILE);
        try {
            if(!f.exists()){
                f.createNewFile();
            }
            scanner = new Scanner(new FileReader(PAYMENTS_FILE));
        } catch (IOException e){
            throw new PersistenceException("Error loading payments file.",e);
        }
        while(scanner.hasNextLine()){
            String currentLine = scanner.nextLine();
            String[] tokens = currentLine.split(DELIMITER);
            if(tokens.length != 5){
                throw new InvalidDataException("Tried to load invalid payment. Possible data corruption.");
            }

            try {
                String id = tokens[0];
                double amount = Double.parseDouble(tokens[1]);
                PaymentMethod method = PaymentMethod.values()[Integer.parseInt(tokens[2])];
                PaymentStatus status = PaymentStatus.values()[Integer.parseInt(tokens[3])];
                int reason = Integer.parseInt(tokens[4]);
                AuthFailureReason failureReason = reason == -1 ? null : AuthFailureReason.values()[reason];
                payments.put(id,Payment.fromPersistance(id,amount,method,status,failureReason));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                throw new InvalidDataException("Tried to load invalid payment. Possible data corruption.", e);
            }
        }
        scanner.close();
    }
    private void writePayment(Payment payment) throws PersistenceException {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(PAYMENTS_FILE, true));
        } catch (IOException e){
            throw new PersistenceException("Could not save payment",e);
        }
        out.println(getEntryString(payment));
        out.flush();
        out.close();
    }
    private String getEntryString(Payment payment){
        int failureReason = -1;
        if(payment.getFailureReason() != null){
            failureReason = payment.getFailureReason().ordinal();
        }
        String text = payment.getId() + DELIMITER
                + payment.getAmount() + DELIMITER
                + payment.getPaymentMethod().ordinal() + DELIMITER
                + payment.getStatus().ordinal() + DELIMITER
                + failureReason;
        return text;
    }




}
