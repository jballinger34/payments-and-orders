package me.jamie.paymentspractice.data.dto;

import me.jamie.paymentspractice.domain.model.LineItem;

public record LineItemDto(String productId, String name, int quantity, double totalPrice) {
    public static LineItemDto from(LineItem item){
        return new LineItemDto(item.getProductId(), item.getProductName(), item.getQuantity(), item.getPriceAtPurchase());
    }

}


// DTO for sending/receiving from frontend
// RECORD for sending/receiving from DB