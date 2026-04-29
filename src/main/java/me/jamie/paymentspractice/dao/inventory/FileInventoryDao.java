package me.jamie.paymentspractice.dao.inventory;

import me.jamie.paymentspractice.exception.DuplicateProductException;
import me.jamie.paymentspractice.exception.InvalidDataException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileInventoryDao implements InventoryDao {

    //EVERY TIME STOCK IS ALTERED, NEW ADDED, ETC - ALL IS REWRITTEN TO FILE
    //BAD PERFORMANCE

    private final String INVENTORY_FILE;
    private final String DELIMITER = "::";
    private final Map<String, Integer> inventory = new HashMap<>();


    public FileInventoryDao(String file) throws PersistenceException {
        this.INVENTORY_FILE = file;
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
        //productId::stockQty
        while(s.hasNextLine()) {
            try{
                String entry = s.nextLine();
                String[] fields = entry.split(DELIMITER);
                String productId = fields[0];
                Integer quantity = Integer.parseInt(fields[1]);
                inventory.put(productId,quantity);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                throw new InvalidDataException("Tried to load invalid inventory. Possible data corruption.", e);
            }
        }
    }
    private void writeAllStock() throws PersistenceException {
        try (PrintWriter out = new PrintWriter(new FileWriter(INVENTORY_FILE))){

            for(String productId : inventory.keySet()){
                out.println(productId + DELIMITER + inventory.get(productId));
            }
            out.flush();

        } catch (IOException e) {
            throw new PersistenceException("Could not save inventory", e);
        }
    }

    @Override
    public int getStock(String productId) throws ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product " + productId + " not in inventory");
        }
        return inventory.get(productId);
    }

    @Override
    public void alterStock(String productId, int quantity) throws PersistenceException, ProductNotFoundException {
        if(!inventory.containsKey(productId)){
            throw new ProductNotFoundException("Product " + productId + " not in inventory");
        }
        int current = inventory.get(productId);
        inventory.put(productId, current + quantity);
        writeAllStock();
    }
    @Override
    public void addProduct(String productId) throws PersistenceException, DuplicateProductException {
        if(inventory.containsKey(productId)){
            throw new DuplicateProductException("Product " + productId + " already exists.");
        }
        inventory.put(productId,0);
        writeAllStock();
    }
}
