package me.jamie.paymentspractice.data.dto;

import me.jamie.paymentspractice.domain.model.Product;

public record ProductDto(String id, String name, double price, int stock){
    public static ProductDto from(Product product){
        return new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
    }

}
