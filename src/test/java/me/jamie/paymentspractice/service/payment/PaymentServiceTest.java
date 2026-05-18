package me.jamie.paymentspractice.service.payment;

import me.jamie.paymentspractice.dao.payment.PaymentDao;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.domain.processor.PaymentProcessor;
import me.jamie.paymentspractice.exception.PaymentNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import me.jamie.paymentspractice.service.PaymentService;
import me.jamie.paymentspractice.service.audit.AuditAction;
import me.jamie.paymentspractice.service.audit.AuditService;
import me.jamie.paymentspractice.service.audit.AuditType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock PaymentDao dao;
    @Mock AuditService auditService;
    @Mock PaymentProcessor processor;

    PaymentService service;

    @BeforeEach
    void setUp(){
        when(processor.supports()).thenReturn(PaymentMethod.CARD);
        service = new PaymentService(dao,auditService, List.of(processor));
    }

    @Test
    void testCreatePayment() throws Exception {

        Payment payment = service.createPayment(100.0, PaymentMethod.CARD);

        assertEquals(100.0, payment.getAmount());
        assertEquals(PaymentMethod.CARD, payment.getPaymentMethod());

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getFailureReason());


        verify(dao).save(payment);
        verify(auditService).logAttempt(any(), any(), any());
        verify(auditService).logSuccess(any(), any(), any());
    }

    //test flow methods.
    // these tests simply check that we are:
    //  delegating correctly to the processor
    //  saving the result
    //  logging correctly


    @Test
    void testAuthSuccessFlow() throws Exception {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1",PaymentStatus.PENDING, null);
        Payment authorised = Payment.fromPersistence("1",100.0, PaymentMethod.CARD, "1",PaymentStatus.AUTHORIZED, null);

        when(processor.authorize(payment)).thenReturn(authorised);

        service.authorizePayment(payment);

        verify(processor).authorize(payment);
        verify(dao).save(authorised);
        verify(auditService).logSuccess(eq(AuditType.PAYMENT), eq(AuditAction.AUTH), eq("1"));
    }

    @Test
    void testAuthFailureFlow() throws Exception {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD,"1", PaymentStatus.PENDING, null);
        Payment declined = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.FAILED, PaymentFailureReason.INSUFFICIENT_FUNDS);

        when(processor.authorize(payment)).thenReturn(declined);

        service.authorizePayment(payment);

        verify(processor).authorize(payment);
        verify(dao).save(declined);
        verify(auditService).logFailure(eq(AuditType.PAYMENT), eq(AuditAction.AUTH), eq("1"), eq(PaymentFailureReason.INSUFFICIENT_FUNDS.toString()));
    }
    @Test
    void testCaptureSuccessFlow() throws Exception {
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.AUTHORIZED, null);
        Payment captured = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.CAPTURED, null);

        when(processor.capture(payment)).thenReturn(captured);

        service.capturePayment(payment);

        verify(processor).capture(payment);
        verify(dao).save(captured);
        verify(auditService).logSuccess(eq(AuditType.PAYMENT), eq(AuditAction.CAPTURE), eq("1"));
    }
    @Test
    void testCaptureFailedFlow() throws Exception {
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.PENDING, null);
        Payment declined = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.FAILED, PaymentFailureReason.FRAUD_SUSPECTED);

        when(processor.capture(payment)).thenReturn(declined);

        service.capturePayment(payment);

        verify(processor).capture(payment);
        verify(dao).save(declined);
        verify(auditService).logFailure(eq(AuditType.PAYMENT), eq(AuditAction.CAPTURE), eq("1"), eq(PaymentFailureReason.FRAUD_SUSPECTED.toString()));
    }

    @Test
    void testMissingProcessor(){
        Payment payment = new Payment("1", 100.0, null);
        assertThrows(IllegalArgumentException.class, () -> service.capturePayment(payment));
    }
    @Test
    void testGetAllFlow() throws Exception {

        List<Payment> payments = List.of(
                new Payment("1", 100.0, PaymentMethod.CARD),
                new Payment("2", 50.0, PaymentMethod.CARD)
        );

        when(dao.findAll()).thenReturn(payments);

        List<Payment> result = service.getAllPayments();

        assertEquals(2, result.size());
        assertEquals(payments, result);

        verify(dao).findAll();
    }

    @Test
    void testPropagatePersistenceException() throws Exception {
        when(dao.findAll()).thenThrow(new PersistenceException("DB failure"));
        assertThrows(PersistenceException.class, () -> service.getAllPayments());
    }

    @Test
    void testGetPayment() throws Exception {

        Payment payment = new Payment("1", 100.0, PaymentMethod.CARD);
        when(dao.findById("1")).thenReturn(payment);

        Payment result = service.getPayment("1");
        assertEquals(payment, result);

        verify(dao).findById("1");
    }


    @Test
    void testPaymentNotFound() throws Exception {
        when(dao.findById("missing")).thenThrow(new PaymentNotFoundException("missing"));
        assertThrows(PaymentNotFoundException.class, () -> service.getPayment("missing"));
    }


    @Test
    void testAuthNullPayment() {
        assertThrows(IllegalArgumentException.class, () -> service.authorizePayment((Payment) null));
    }

    @Test
    void testProcessorNotSupportingMethod() {
        Payment payment = mock(Payment.class);
        when(payment.getPaymentMethod()).thenReturn(PaymentMethod.CARD);

        PaymentProcessor otherProcessor = mock(PaymentProcessor.class);
        when(otherProcessor.supports()).thenReturn(PaymentMethod.BANK_TRANSFER);

        PaymentService service = new PaymentService(dao, auditService, List.of(otherProcessor));

        assertThrows(IllegalArgumentException.class, () -> service.authorizePayment(payment));
    }

}
