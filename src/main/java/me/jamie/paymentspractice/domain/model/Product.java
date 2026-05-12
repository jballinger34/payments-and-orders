package me.jamie.paymentspractice.domain.model;

import me.jamie.paymentspractice.data.entity.ProductEntity;

public class Product {

    private final String id;
    private String name;
    private double cost;
    private int stock;
    
    public Product(String id, String name, double cost, int stock){
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }

    public static Product fromPersistence(ProductEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getCost(), entity.getStock());
    }

    public String getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setName(String name) {
        this.name = name;
    }
}
