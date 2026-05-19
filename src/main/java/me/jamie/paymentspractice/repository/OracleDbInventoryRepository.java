package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.data.entity.ProductEntity;
import me.jamie.paymentspractice.exception.ProductNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OracleDbInventoryRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByMerchantIdAndId(String merchantId, String productId);
    List<ProductEntity> findByMerchantId(String merchantId);
    void deleteByMerchantIdAndId(String merchantId, String productId);
}
