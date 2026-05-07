package me.jamie.paymentspractice.dto;

import me.jamie.paymentspractice.domain.model.order.Order;

import java.util.List;

public record OrderDto (String id, List<LineItemDto> items, double total, String paymentId, String status){

    public static OrderDto from(Order order){
        List<LineItemDto> items = order.getItems().stream().map(LineItemDto::from).toList();
        return new OrderDto(order.getId(), items, order.getTotal(), order.getPayment().getId(), order.getStatus().toString());
    }

}
