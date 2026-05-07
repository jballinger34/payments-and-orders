package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.dto.ProductDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getInventory() throws PersistenceException {
        return service.getAllProducts()
                .stream()
                .map(product -> new ProductDto(
                        product.getId(),
                        product.getName(),
                        product.getCost(),
                        product.getStock()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable String id) throws PersistenceException, ProductNotFoundException {
        Product product = service.getProduct(id);
        return new ProductDto(product.getId(),product.getName(), product.getCost(), product.getStock());
    }

    @PostMapping("/{id}/restock")
    public ProductDto restockProduct(@PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.restockProduct(id,amount);
        return new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
    }

    @PostMapping
    public ProductDto stockProduct(@RequestParam String name,
                                   @RequestParam double cost,
                                   @RequestParam int stock) throws PersistenceException{
        Product product = service.createProduct(name, cost, stock);
        return new ProductDto(product.getId(), product.getName(), product.getStock(), product.getStock());
    }

    //TODO
    // decide what response should be returned here

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) throws PersistenceException {
        service.deleteProduct(id);
    }

    @PutMapping("/{id}/stock")
    public ProductDto setStock(@PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.setStock(id, amount);
        return new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
    }

    @PutMapping("/{id}/price")
    public ProductDto setPrice(@PathVariable String id, @RequestParam double price) throws PersistenceException {
        Product product = service.setPrice(id, price);
        return new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
    }
    @PutMapping("/{id}/name")
    public ProductDto setName(@PathVariable String id, @RequestParam String name) throws PersistenceException {
        Product product = service.setName(id, name);
        return new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
    }

}
