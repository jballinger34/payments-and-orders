package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.data.dto.ProductDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.InventoryService;
import me.jamie.paymentspractice.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BrowseController {

    private InventoryService inventoryService;

    public BrowseController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/browse")
    public String browse(Model model) throws PersistenceException {

        model.addAttribute("products", inventoryService.getAllProducts().stream().map(ProductDto::from).toList());

        return "browse";
    }

}
