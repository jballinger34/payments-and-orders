package me.jamie.paymentspractice.domain.processor;

import me.jamie.paymentspractice.domain.model.payment.*;
import me.jamie.paymentspractice.gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardPaymentProcessorTest {

    @Mock PaymentGateway gateway;

    CardPaymentProcessor processor;

    @BeforeEach
    void setUp(){
        processor = new CardPaymentProcessor(gateway);
    }

    @Test
    void testAuthoriseSuccess(){
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null, PaymentStatus.PENDING, null);
        PaymentResponse response = new PaymentResponse(true,null,"1");

        when(gateway.authorize(payment)).thenReturn(response);

        Payment result = processor.authorize(payment);

        assertEquals(PaymentStatus.AUTHORIZED, result.getStatus());
        assertEquals("1", payment.getProviderReference());
        assertNull(payment.getFailureReason());

        verify(gateway).authorize(payment);
    }
    @Test
    void testAuthoriseFailure(){
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null, PaymentStatus.PENDING, null);
        PaymentResponse response = new PaymentResponse(false, PaymentFailureReason.INSUFFICIENT_FUNDS,null);

        when(gateway.authorize(payment)).thenReturn(response);

        Payment result = processor.authorize(payment);

        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertEquals(PaymentFailureReason.INSUFFICIENT_FUNDS, payment.getFailureReason());

        verify(gateway).authorize(payment);
    }
    @Test
    void testCaptureSuccess() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.AUTHORIZED, null);

        PaymentResponse response = new PaymentResponse(true, null, "1");

        when(gateway.capture(payment)).thenReturn(response);

        Payment result = processor.capture(payment);

        assertEquals(PaymentStatus.CAPTURED, result.getStatus());
        assertEquals("1", payment.getProviderReference());
        assertNull(payment.getFailureReason());

        verify(gateway).capture(payment);
    }

    @Test
    void captureFailure() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "1", PaymentStatus.AUTHORIZED, null);

        PaymentResponse response = new PaymentResponse(false, PaymentFailureReason.FRAUD_SUSPECTED, "1");

        when(gateway.capture(payment)).thenReturn(response);

        Payment result = processor.capture(payment);

        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertEquals(PaymentFailureReason.FRAUD_SUSPECTED, result.getFailureReason());

        verify(gateway).capture(payment);
    }

}
