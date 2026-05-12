package me.jamie.paymentspractice.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProductEntity {

    @Id
    private String id;
    private String name;
    private double cost;
    private int stock;

    public ProductEntity(){}
    public ProductEntity(String id, int stock, double cost, String name) {
        this.id = id;
        this.stock = stock;
        this.cost = cost;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
