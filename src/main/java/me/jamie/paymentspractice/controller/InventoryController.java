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
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service){
        this.service = service;
    }

    @GetMapping("/{merchantId}/inventory")
    public ResponseEntity<List<ProductDto>> getInventory(@PathVariable String merchantId) throws PersistenceException {
        return ResponseEntity.ok(
                service.getAllProducts(merchantId)
                    .stream()
                    .map(ProductDto::from)
                    .toList()
        );
    }

    @GetMapping("/{merchantId}/inventory/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String merchantId, @PathVariable String id) throws PersistenceException, ProductNotFoundException {
        Product product = service.getProduct(merchantId ,id);
        ProductDto dto = new ProductDto(product.getId(),product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("{merchantId}/inventory/{id}/restock")
    public ResponseEntity<ProductDto> restockProduct(@PathVariable String merchantId, @PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.restockProduct(merchantId,id,amount);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{merchantId}/inventory")
    public ResponseEntity<ProductDto> createProduct(@PathVariable String merchantId,
                                                    @RequestParam String name,
                                                    @RequestParam double cost,
                                                    @RequestParam int stock) throws PersistenceException{
        Product product = service.createProduct(merchantId, name, cost, stock);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{merchantId}/inventory/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String merchantId, @PathVariable String id) throws PersistenceException {
        service.deleteProduct(merchantId, id);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{merchantId}/inventory/{id}/stock")
    public ResponseEntity<ProductDto> setStock(@PathVariable String merchantId,@PathVariable String id, @RequestParam int amount) throws PersistenceException {
        Product product = service.setStock(merchantId, id, amount);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);

    }

    @PutMapping("/{merchantId}/inventory/{id}/price")
    public ResponseEntity<ProductDto> setPrice(@PathVariable String merchantId, @PathVariable String id, @RequestParam double price) throws PersistenceException {
        Product product = service.setPrice(merchantId, id, price);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{merchantId}/inventory/{id}/name")
    public ResponseEntity<ProductDto> setName(@PathVariable String merchantId, @PathVariable String id, @RequestParam String name) throws PersistenceException {
        Product product = service.setName(merchantId, id, name);
        ProductDto dto = new ProductDto(product.getId(), product.getName(), product.getCost(), product.getStock());
        return ResponseEntity.ok(dto);
    }

}
