package me.jamie.paymentspractice.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import me.jamie.paymentspractice.domain.model.Product;

@Entity
public class ProductEntity {

    @Id
    private String id;
    private String merchantId;
    private String name;
    private double cost;
    private int stock;

    public ProductEntity(){}
    public ProductEntity(String id, String merchantId,String name, double cost, int stock) {
        this.id = id;
        this.merchantId = merchantId;
        this.stock = stock;
        this.cost = cost;
        this.name = name;
    }

    public static ProductEntity from(Product product){
        return new ProductEntity(product.getId(), product.getMerchantId(), product.getName(), product.getCost(), product.getStock());
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

    public String getMerchantId() {
        return merchantId;
    }
}
