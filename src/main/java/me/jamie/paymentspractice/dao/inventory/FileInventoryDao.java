package me.jamie.paymentspractice.dao.inventory;

import jakarta.annotation.PostConstruct;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.InvalidDataException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileInventoryDao implements InventoryDao {


    private final String INVENTORY_FILE;
    private final String DELIMITER = "::";
    private final Map<String, Product> inventory = new HashMap<>();


    public FileInventoryDao(@Value("${dao.inventory.file}") String file) {
        this.INVENTORY_FILE = file;
    }

    @PostConstruct
    public void init() throws PersistenceException {
        loadStock();
    }

    private void loadStock() throws PersistenceException {
        //get file from String, check if it exists, if not create it
        //get Scanner to read it
        if(INVENTORY_FILE == null) return;
        File f = new File(INVENTORY_FILE);
        Scanner s;
        try{
            if(!f.exists()){
                f.createNewFile();
            }
            s = new Scanner(new FileReader(INVENTORY_FILE));
        } catch (IOException e){
            throw new PersistenceException("Error loading inventory file.", e);
        }

        //saved in format
        //productId::name::cost::stockQty
        while(s.hasNextLine()) {
            try{
                String entry = s.nextLine();
                String[] fields = entry.split(DELIMITER);

                String productId = fields[0];
                String name = fields[1];
                double cost = Double.parseDouble(fields[2]);
                int quantity = Integer.parseInt(fields[3]);

                inventory.put(productId,new Product(productId,name, cost,quantity));

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                throw new InvalidDataException("Tried to load invalid inventory. Possible data corruption.", e);
            }
        }
    }
    private void writeAllStock() throws PersistenceException {
        try (PrintWriter out = new PrintWriter(new FileWriter(INVENTORY_FILE))){

            for(Product product : inventory.values()){
                out.println(product.getId()+ DELIMITER + product.getName() + DELIMITER + product.getCost()+ DELIMITER + product.getStock());
            }
            out.flush();

        } catch (IOException e) {
            throw new PersistenceException("Could not save inventory", e);
        }
    }

    @Override
    public List<Product> findAll(){
        return new ArrayList<>(inventory.values());
    }
    @Override
    public Product findById(String productId) throws ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product " + productId + " not in inventory");
        }
        return inventory.get(productId);
    }
    @Override
    public void put(String productId, Product product) throws PersistenceException {
        inventory.put(productId, product);
        writeAllStock();
    }
    @Override
    public void remove(String productId) throws PersistenceException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product " + productId + " not in inventory");
        }
        inventory.remove(productId);
        writeAllStock();
    }
}
