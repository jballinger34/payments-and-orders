package me.jamie.paymentspractice.dao.payment;

import jakarta.annotation.PostConstruct;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.exception.InvalidDataException;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FilePaymentDao implements PaymentDao {

    // currently when we load from file we load the same payment multiple times
    // but it is overriden at each step because keys are unique (and stay the same for a payment)
    private Map<String, Payment> payments = new HashMap<>();

    private static final String DELIMITER = "::";
    private final String paymentsFile;

    public FilePaymentDao(@Value("${dao.payments.file}") String paymentsFile){
        this.paymentsFile = paymentsFile;
    }


    @PostConstruct
    public void init() throws PersistenceException {
        loadPayments();
    }

    @Override
    public void save(Payment payment) throws PersistenceException {
        payments.put(payment.getId(), payment);
        writePayment(payment);
    }

    @Override
    public Payment findById(String id) throws PaymentNotFoundException {
        Payment payment = payments.get(id);
        if(payment == null) throw new PaymentNotFoundException(id);
        return payment;
    }

    @Override
    public List<Payment> findAll(){
        return new ArrayList<>(payments.values());
    }

    private void loadPayments() throws PersistenceException, InvalidDataException {
        Scanner scanner;
        File f = new File(paymentsFile);
        try {
            if(!f.exists()){
                f.createNewFile();
            }
            scanner = new Scanner(new FileReader(paymentsFile));
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
                PaymentFailureReason failureReason = reason == -1 ? null : PaymentFailureReason.values()[reason];
                payments.put(id,Payment.fromPersistence(id,amount,method,status,failureReason));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                throw new InvalidDataException("Tried to load invalid payment. Possible data corruption.", e);
            }
        }
        scanner.close();
    }
    private void writePayment(Payment payment) throws PersistenceException {
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(paymentsFile, true));
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
