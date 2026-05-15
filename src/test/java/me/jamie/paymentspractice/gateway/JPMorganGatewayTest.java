package me.jamie.paymentspractice.gateway;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import me.jamie.paymentspractice.gateway.jpm.JPMHttpClient;
import me.jamie.paymentspractice.gateway.jpm.JPMorganGateway;
import me.jamie.paymentspractice.gateway.jpm.request.JPMRequest;
import me.jamie.paymentspractice.gateway.jpm.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.gateway.jpm.response.JPMCaptureResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JPMorganGatewayTest {

    @Mock
    private JPMHttpClient client;

    @InjectMocks
    private JPMorganGateway gateway;

    @Test
    void testAuthSuccess() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null,
                PaymentStatus.PENDING, null);

        when(client.sendAuthorizeRequest(any(JPMRequest.class)))
                .thenReturn(new JPMAuthoriseResponse("txn_123", "SUCCESS", "00", "Approved"));

        PaymentResponse response = gateway.authorize(payment);

        assertTrue(response.isSuccessful());
        assertNull(response.getFailureReason());
        assertEquals("txn_123", response.getProviderReference());
    }

    @Test
    void testAuthInsufficientFunds() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null,
                PaymentStatus.PENDING,null);

        when(client.sendAuthorizeRequest(any(JPMRequest.class)))
                .thenReturn(new JPMAuthoriseResponse("txn_456", "DECLINED", "INSUFFICIENT_FUNDS", "Insufficient funds"));

        PaymentResponse response = gateway.authorize(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.INSUFFICIENT_FUNDS, response.getFailureReason());
        assertEquals("txn_456", response.getProviderReference());
    }
    @Test
    void testAuthNullResponse(){
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null,
                PaymentStatus.PENDING,null);

        when(client.sendAuthorizeRequest(any(JPMRequest.class))).thenReturn(null);

        PaymentResponse response = gateway.authorize(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.PROCESSOR_ERROR, response.getFailureReason());
        assertNull(response.getProviderReference());
    }

    @Test
    void testWhenAuthThrowsException() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, null,
                PaymentStatus.PENDING, null);

        when(client.sendAuthorizeRequest(ArgumentMatchers.any(JPMRequest.class)))
                .thenThrow(new RuntimeException("HTTP failure"));

        PaymentResponse response = gateway.authorize(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.PROCESSOR_ERROR, response.getFailureReason());
        assertNull(response.getProviderReference());
    }

    @Test
    void testCaptureSuccess() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD,"txn_123",
                PaymentStatus.AUTHORIZED, null);

        when(client.sendCaptureRequest(eq("txn_123"), any(JPMRequest.class))).
                thenReturn(new JPMCaptureResponse("txn_capture_1", "SUCCESS", "00", "Capture approved"));

        PaymentResponse response = gateway.capture(payment);

        assertTrue(response.isSuccessful());
        assertNull(response.getFailureReason());
        assertEquals("txn_capture_1", response.getProviderReference());
    }

    @Test
    void testCaptureDeclined() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "txn_123",
                PaymentStatus.AUTHORIZED, null);

        when(client.sendCaptureRequest(eq("txn_123"), any(JPMRequest.class)))
                .thenReturn(new JPMCaptureResponse("txn_capture_2", "DECLINED", "DECLINED_CVV", "Invalid CVV"));

        PaymentResponse response = gateway.capture(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.INCORRECT_CVV, response.getFailureReason());
        assertEquals("txn_capture_2", response.getProviderReference());
    }

    @Test
    void testCaptureNullResponse(){
        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "txn_123",
                PaymentStatus.AUTHORIZED, null);

        when(client.sendCaptureRequest(eq("txn_123"), any(JPMRequest.class))).thenReturn(null);

        PaymentResponse response = gateway.capture(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.PROCESSOR_ERROR, response.getFailureReason());
        assertEquals("txn_123", response.getProviderReference());
    }
    @Test
    void testWhenCaptureThrowsException() {

        Payment payment = Payment.fromPersistence("1", 100.0, PaymentMethod.CARD, "txn_123",
                PaymentStatus.AUTHORIZED, null);

        when(client.sendCaptureRequest(eq("txn_123"), any(JPMRequest.class))).thenThrow(new RuntimeException("HTTP failure"));

        PaymentResponse response = gateway.capture(payment);

        assertFalse(response.isSuccessful());
        assertEquals(PaymentFailureReason.PROCESSOR_ERROR, response.getFailureReason());
        assertEquals("txn_123", response.getProviderReference());
    }
}