package me.jamie.paymentspractice.dao.product;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.exception.InvalidDataException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;

import java.io.*;
import java.util.*;

public class FileProductDao implements ProductDao {

    private final String PRODUCTS_FILE;
    private final String DELIMITER = "::";

    private final Map<String,Product> products = new HashMap<>();


    public FileProductDao(String products_file) throws PersistenceException {
        this.PRODUCTS_FILE = products_file;
        loadProducts();
    }

    private void loadProducts() throws PersistenceException {
        //get file from String, check if it exists, if not create it
        //get Scanner to read it
        if(PRODUCTS_FILE == null) return;
        File f = new File(PRODUCTS_FILE);
        Scanner s;
        try{
            if(!f.exists()){
                f.createNewFile();
            }
            s = new Scanner(new FileReader(PRODUCTS_FILE));
        } catch (IOException e){
            throw new PersistenceException("Error loading products file.", e);
        }

        //saved in format
        //id::name::cost
        while(s.hasNextLine()) {
            try{
                String entry = s.nextLine();
                String[] fields = entry.split(DELIMITER);
                String productId = fields[0];
                String name = fields[1];
                double cost = Double.parseDouble(fields[2]);
                products.put(productId, new Product(productId,name,cost));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e){
                throw new InvalidDataException("Tried to load invalid product. Possible data corruption.", e);
            }
        }
    }
    private void writeAllProducts() throws PersistenceException {
        try (PrintWriter out = new PrintWriter(new FileWriter(PRODUCTS_FILE))){

            for(Product product : products.values()){
                out.println(product.getId() + DELIMITER + product.getName() + DELIMITER + product.getCost());
            }
            out.flush();

        } catch (IOException e) {
            throw new PersistenceException("Could not save products", e);
        }
    }

    @Override
    public void put(Product product) throws PersistenceException {
        products.put(product.getId(), product);
        writeAllProducts();
    }

    @Override
    public void remove(String productId) throws PersistenceException {
        if(!products.containsKey(productId)){
            throw new ProductNotFoundException("Tried to remove product " + productId + " that doesn't exist.");
        }
        products.remove(productId);
        writeAllProducts();
    }

    @Override
    public Product findById(String productId) {
        if(!products.containsKey(productId)){
            throw new ProductNotFoundException("Product " + productId + " doesn't exist.");
        }
        return products.get(productId);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
}
