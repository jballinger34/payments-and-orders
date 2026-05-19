package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.ProductDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BrowseController {

    private InventoryService inventoryService;

    public BrowseController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{merchantId}/browse")
    public String browse(@PathVariable String merchantId, Model model) throws PersistenceException {

        model.addAttribute("products", inventoryService.getAllProducts(merchantId).stream().map(ProductDto::from).toList());

        return "browse";
    }

}
