package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.OrderDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    //TODO pass status to filter by status

    @GetMapping("/{merchantId}/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders(@PathVariable String merchantId) throws PersistenceException {
        List<OrderDto> orderDtos = orderService.getAllOrders(merchantId).stream().map(OrderDto::from).toList();
        return ResponseEntity.ok(orderDtos);
    }
    @GetMapping("/{merchantId}/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String merchantId, @PathVariable String id) throws PersistenceException {
        OrderDto orderDto = orderService.getOrder(merchantId, id);
        return ResponseEntity.ok(orderDto);
    }

    @PutMapping("/{merchantId}/orders/{id}/fulfil")
    public ResponseEntity<OrderDto> markFulfilled(@PathVariable String merchantId, @PathVariable String id) throws PersistenceException {
        OrderDto dto = orderService.fulfil(merchantId, id);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{merchantId}/orders/{id}/complete")
    public ResponseEntity<OrderDto> markCompleted(@PathVariable String merchantId, @PathVariable String id) throws PersistenceException {
        OrderDto dto = orderService.complete(merchantId, id);
        return ResponseEntity.ok(dto);
    }

}
