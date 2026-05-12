package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.data.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OracleDbInventoryRepository extends JpaRepository<ProductEntity, String> {
}
