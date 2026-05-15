package me.jamie.paymentspractice.gateway.jpm;

import me.jamie.paymentspractice.gateway.jpm.request.JPMRequest;
import me.jamie.paymentspractice.gateway.jpm.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;
import me.jamie.paymentspractice.gateway.jpm.response.JPMCaptureResponse;
import me.jamie.paymentspractice.gateway.PaymentGateway;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class JPMorganGateway implements PaymentGateway {

    private final JPMHttpClient client;


    public JPMorganGateway(JPMHttpClient client){
        this.client = client;
    }


    @Override
    public PaymentResponse authorize(Payment payment) {
        JPMRequest request = RequestFactory.buildAuthRequest(payment);
        try{
            JPMAuthoriseResponse response = client.sendAuthorizeRequest(request);

            if("SUCCESS".equals(response.responseStatus())){
                return new PaymentResponse(true,null, response.transactionId());
            } else {
                PaymentFailureReason reason = mapReason(response.responseCode());
                return new PaymentResponse(false, reason, response.transactionId());
            }

        } catch (Exception e){
            return new PaymentResponse(false, PaymentFailureReason.PROCESSOR_ERROR, null);
        }

    }

    @Override
    public PaymentResponse capture(Payment payment) {
        JPMRequest request = RequestFactory.buildCaptureRequest(payment);
        try{
            JPMCaptureResponse response = client.sendCaptureRequest(payment.getProviderReference(), request);

            if("SUCCESS".equals(response.responseStatus())){
                return new PaymentResponse(true,null, response.transactionId());
            } else {
                PaymentFailureReason reason = mapReason(response.responseCode());
                return new PaymentResponse(false, reason, response.transactionId());
            }
        } catch (Exception e){
            return new PaymentResponse(false, PaymentFailureReason.PROCESSOR_ERROR, payment.getProviderReference());
        }
    }

    private PaymentFailureReason mapReason(String responseCode){
        return switch (responseCode) {
            case "INSUFFICIENT_FUNDS" -> PaymentFailureReason.INSUFFICIENT_FUNDS;
            case "CARD_EXPIRED" -> PaymentFailureReason.EXPIRED_CARD;
            case "TIMEOUT", "ISSUER_TIMEOUT", "PAYMENT_REQUEST_EXPIRED" -> PaymentFailureReason.TIMEOUT;
            case "DECLINED_INVALID_CVV", "DECLINED_CVV", "DECLINED_AVS_CVV" -> PaymentFailureReason.INCORRECT_CVV;
            default -> PaymentFailureReason.PROCESSOR_ERROR;
        };
    }


    //static inner class to help build the requests to auth/capture
    //keeps general buildRequest function hidden, so this factory
    //controls the captureMethod string
    static class RequestFactory{
        public static JPMRequest buildAuthRequest(Payment payment){
            return buildRequest(payment, "MANUAL");
        }
        public static JPMRequest buildCaptureRequest(Payment payment){
            return buildRequest(payment,"NOW");
        }
        private static JPMRequest buildRequest(Payment payment, String captureMethod){
            return new JPMRequest(
                    captureMethod,
                    (int)(payment.getAmount()*100),
                    "GBP",
                    new JPMRequest.Merchant(
                            new JPMRequest.MerchantSoftware(
                                    "Jamie Test Payments Ltd",
                                    "Payments Practice"
                            )
                    ),
                    new JPMRequest.PaymentMethodType(
                            new JPMRequest.Card(
                                    //TEST FOR NOW
                                    "4012000033330026",
                                    new JPMRequest.Expiry(
                                            5,
                                            2025
                                    )
                            )
                    )
            );
        }

    }

}
