package me.jamie.paymentspractice.dto;

public record LineItemDto(String productId, String name, int quantity, double unitPrice, double totalPrice) {
}


// DTO for sending/receiving from frontend
// RECORD for sending/receiving from DB