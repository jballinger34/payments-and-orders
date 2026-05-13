package me.jamie.paymentspractice.controller;

import me.jamie.paymentspractice.domain.model.Product;
import me.jamie.paymentspractice.data.dto.ProductDto;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import me.jamie.paymentspractice.service.InventoryService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ProductDto>> getInventory() throws PersistenceException {
        return ResponseEntity.ok(
                service.getAllProducts()
                    .stream()
                    .map(ProductDto::from)
                    .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) throws PersistenceException, ProductNotFoundException {
        Product product = service.getProduct(id);
        ProductDto dto = new ProductDto(product.getId(),product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<ProductDto> restockProduct(@PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.restockProduct(id,amount);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestParam String name,
                                   @RequestParam double cost,
                                   @RequestParam int stock) throws PersistenceException{
        Product product = service.createProduct(name, cost, stock);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) throws PersistenceException {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto> setStock(@PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.setStock(id, amount);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);

    }

    @PutMapping("/{id}/price")
    public ResponseEntity<ProductDto> setPrice(@PathVariable String id, @RequestParam double price) throws PersistenceException {
        Product product = service.setPrice(id, price);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{id}/name")
    public ResponseEntity<ProductDto> setName(@PathVariable String id, @RequestParam String name) throws PersistenceException {
        Product product = service.setName(id, name);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

}
