package me.jamie.paymentspractice.dao;

import me.jamie.paymentspractice.dao.payment.OracleDbPaymentDao;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OracleDbPaymentDaoTest {

    @Autowired
    OracleDbPaymentDao paymentDao;

    @Test
    void testSaveAndFindById() throws Exception {

        Payment payment = new Payment(
                "PAYMENT_1",
                25.50,
                PaymentMethod.CARD
        );
        paymentDao.save(payment);

        Payment found = paymentDao.findById("PAYMENT_1");

        assertNotNull(found);
        assertEquals(payment.getId(), found.getId());
        assertEquals(payment.getAmount(), found.getAmount());
        assertEquals(payment.getPaymentMethod(), found.getPaymentMethod());
        assertEquals(payment.getStatus(), found.getStatus());
    }

    @Test
    void testFindAll() throws Exception {

        Payment payment1 = new Payment(
                "PAYMENT_2",
                10.00,
                PaymentMethod.CARD
        );

        Payment payment2 = new Payment(
                "PAYMENT_3",
                15.00,
                PaymentMethod.BANK_TRANSFER
        );

        paymentDao.save(payment1);
        paymentDao.save(payment2);

        List<Payment> payments = paymentDao.findAll();

        assertNotNull(payments);

        assertTrue(
                payments.stream().anyMatch(p -> p.getId().equals("PAYMENT_2"))
        );

        assertTrue(
                payments.stream().anyMatch(p -> p.getId().equals("PAYMENT_3"))
        );
    }

    @Test
    void testFindByIdThrowsWhenPaymentNotFound() {
        assertThrows(
                PaymentNotFoundException.class, () -> paymentDao.findById("DOES_NOT_EXIST")
        );
    }

}
