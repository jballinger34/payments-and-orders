package me.jamie.paymentspractice.repository;

import me.jamie.paymentspractice.dto.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleDbPaymentRepository extends JpaRepository<PaymentRecord,String> {
}
