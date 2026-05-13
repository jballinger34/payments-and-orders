package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.data.request.CheckoutItemRequest;
import me.jamie.paymentspractice.data.dto.OrderDto;
import me.jamie.paymentspractice.data.dto.ProductDto;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {

    OrderService orderService;


    public CustomerController(OrderService orderService){
        this.orderService = orderService;
    }

    // TODO IMPORTANT!!!
    //  payment details go here
    //   we take payment details (or a request that represents them here)
    //     and use that to place order (instead of just using card method)

    // other things to consider
    // whether cart is empty? need to handle this
    // whether there is enough stock?
    // taking discount code? apply it in place order?

    //controller shouldnt know checkout sequencing, call a service .checkout()
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(@RequestBody List<CheckoutItemRequest> cart)
            throws PersistenceException, InsufficientStockException {

        Order order = orderService.placeOrder(cart, PaymentMethod.CARD);

        orderService.authorizePayment(order);
        orderService.reserveStock(order);
        orderService.capturePayment(order);

        return ResponseEntity.ok(OrderDto.from(order));
    }
}
