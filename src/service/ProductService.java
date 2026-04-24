package service;

import dao.product.ProductDao;
import domain.model.Product;
import service.audit.AuditService;

import java.util.List;

public class ProductService {

    ProductDao productDao;

    public ProductService(ProductDao productDao){
        this.productDao = productDao;
    }

    public Product getProduct(String productId){
        return productDao.findById(productId);
    }
    public List<Product> getAllProducts(){
        return productDao.findAll();
    }


}
