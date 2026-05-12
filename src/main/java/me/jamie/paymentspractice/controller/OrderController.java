package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.OrderDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    //TODO pass status to filter by status

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() throws PersistenceException {
        List<OrderDto> orderDtos = orderService.getAllOrders().stream().map(OrderDto::from).toList();
        return ResponseEntity.ok(orderDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id) throws PersistenceException {
        OrderDto orderDto = orderService.getOrder(id);
        return ResponseEntity.ok(orderDto);
    }

    @PutMapping("/{id}/fulfil")
    public ResponseEntity<OrderDto> markFulfilled(@PathVariable String id) throws PersistenceException {
        OrderDto dto = orderService.fulfil(id);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{id}/complete")
    public ResponseEntity<OrderDto> markCompleted(@PathVariable String id) throws PersistenceException {
        OrderDto dto = orderService.complete(id);
        return ResponseEntity.ok(dto);
    }

}
