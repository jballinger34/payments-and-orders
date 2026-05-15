package me.jamie.paymentspractice.domain.payment;


import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentMethod;
import me.jamie.paymentspractice.domain.model.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    //auth changes state
    //auth fail throws IllegalStateException and doesnt change state

    //capture changes state
    //cant capture pending

    //fail changes state
    //cant fail captured payment


    @Test
    void testAuthoriseChangesState(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,null
                ,PaymentStatus.PENDING, null);
        payment.authorize("ref");

        assertEquals(PaymentStatus.AUTHORIZED, payment.getStatus());
        assertEquals("ref", payment.getProviderReference());
    }
    @Test
    void testAuthoriseInvalidStatus(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,"initial"
                ,PaymentStatus.CAPTURED, null);

        assertThrows(IllegalStateException.class, () -> payment.authorize("new"));

        assertEquals(PaymentStatus.CAPTURED, payment.getStatus());
        assertEquals("initial", payment.getProviderReference());

    }
    @Test
    void testCaptureChangesState(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,"initial"
                ,PaymentStatus.AUTHORIZED, null);

        payment.capture("new");

        assertEquals(PaymentStatus.CAPTURED, payment.getStatus());
        assertEquals("new", payment.getProviderReference());
    }

    @Test
    void testCapturePendingPayment(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,null
                ,PaymentStatus.PENDING, null);

        assertThrows(IllegalStateException.class, () -> payment.capture("any"));

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }
    @Test
    void testFailChangesState(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,null
                ,PaymentStatus.PENDING, null);

        payment.fail(PaymentFailureReason.INSUFFICIENT_FUNDS);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertNull(payment.getProviderReference());
    }
    @Test
    void testFailCapturedPayment(){
        Payment payment = Payment.fromPersistence("1", 100, PaymentMethod.CARD,null
                ,PaymentStatus.CAPTURED, null);

        assertThrows(IllegalStateException.class, () -> payment.fail(PaymentFailureReason.INSUFFICIENT_FUNDS));

        assertEquals(PaymentStatus.CAPTURED, payment.getStatus());
        assertNull(payment.getFailureReason());
    }




}
