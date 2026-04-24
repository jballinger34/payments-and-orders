package service;

import dao.product.ProductDao;
import domain.model.Product;
import service.audit.AuditService;

import java.util.List;

public class ProductService {

    ProductDao productDao;
    AuditService audit;

    public ProductService(ProductDao productDao, AuditService auditService){
        this.productDao = productDao;
        this.audit = auditService;
    }

    public Product getProduct(String productId){
        return productDao.findById(productId);
    }
    public List<Product> getAllProducts(){
        return productDao.findAll();
    }


}
