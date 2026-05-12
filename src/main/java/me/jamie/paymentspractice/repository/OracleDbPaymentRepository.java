package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.data.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleDbPaymentRepository extends JpaRepository<PaymentEntity,String> {
}
