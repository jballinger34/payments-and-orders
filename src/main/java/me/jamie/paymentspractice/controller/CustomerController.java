package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.LineItem;
import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.domain.model.order.Order;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.dto.CheckoutItemRequest;
import me.jamie.paymentspractice.dto.LineItemDto;
import me.jamie.paymentspractice.exception.InsufficientStockException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import me.jamie.paymentspractice.view.CustomerView;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerController {

    InventoryService inventoryService;
    OrderService orderService;


    public CustomerController(InventoryService inventoryService, OrderService orderService){
        this.inventoryService = inventoryService;
        this.orderService = orderService;
    }

    @GetMapping("/products")
    public List<Product> browse() throws PersistenceException {
        return inventoryService.getAllProducts();
    }

    @PostMapping("/line-items")
    public LineItemDto addToCart(@RequestParam String productId,
                              @RequestParam int quantity)
            throws PersistenceException, ProductNotFoundException {

        Product product = inventoryService.getProduct(productId);

        return new LineItemDto(productId, product.getName(), quantity, product.getCost(), product.getCost()*quantity);
    }

    // TODO IMPORTANT!!!
    //  payment details go here
    //   we take payment details (or a request that represents them here)
    //     and use that to place order (instead of just using card method)

    // other things to consider
    // whether cart is empty? need to handle this
    // whether there is enough stock?
    // taking discount code? apply it in place order?
    @PostMapping("/checkout")
    public Order checkout(@RequestBody List<CheckoutItemRequest> cart)
            throws PersistenceException {

        Order order = orderService.placeOrder(cart, PaymentMethod.CARD);

        orderService.authorizePayment(order);
        orderService.reserveStock(order);
        orderService.capturePayment(order);

        return order;
    }
}
