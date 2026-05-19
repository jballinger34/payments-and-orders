package me.jamie.paymentspractice.domain.model;

import me.jamie.paymentspractice.data.entity.ProductEntity;

public class Product {

    private final String id;
    private final String merchantId;
    private String name;
    private double cost;
    private int stock;
    
    public Product(String id, String merchantId, String name, double cost, int stock){
        this.id = id;
        this.merchantId = merchantId;
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }

    public static Product fromPersistence(ProductEntity entity) {
        return new Product(entity.getId(), entity.getMerchantId(), entity.getName(), entity.getCost(), entity.getStock());
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

    public String getMerchantId() {
        return merchantId;
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
