package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.service.InventoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service){
        this.service = service;
    }


}
