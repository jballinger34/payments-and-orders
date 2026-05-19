package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.data.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OracleDbOrderRepository extends JpaRepository<OrderEntity,String> {
    Optional<OrderEntity> findByMerchantIdAndId(String merchantId, String id);

    List<OrderEntity> findByMerchantId(String merchantId);
}
