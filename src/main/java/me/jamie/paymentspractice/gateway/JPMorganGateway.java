package me.jamie.paymentspractice.gateway;

import me.jamie.paymentspractice.gateway.request.JPMRequest;
import me.jamie.paymentspractice.gateway.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;
import me.jamie.paymentspractice.gateway.response.JPMCaptureResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Primary
@Component
public class JPMorganGateway implements PaymentGateway {

    private final RestClient client;
    //where we get a JPM API key from to auth/capture payments with them
    private final JPMAuthService authService;


    public JPMorganGateway(JPMAuthService authService){
        this.authService = authService;

        this.client = RestClient.builder()
                .baseUrl("https://api-mock.payments.jpmorgan.com/api/v2")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    @Override
    public PaymentResponse authorize(Payment payment) {
        JPMRequest request = RequestFactory.buildAuthRequest(payment);
        try{
            JPMAuthoriseResponse response = client.post().uri("/payments")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                    .header("merchant-id", "998482157630")
                    .header("request-id", UUID.randomUUID().toString())
                    .body(request)
                    .retrieve()
                    .body(JPMAuthoriseResponse.class);

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
            JPMCaptureResponse response = client.post().uri("/payments/"+payment.getProviderReference()+"/captures")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                    .header("merchant-id", "998482157630")
                    .header("request-id", UUID.randomUUID().toString())
                    .body(request)
                    .retrieve()
                    .body(JPMCaptureResponse.class);

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
        switch(responseCode){
            case "INSUFFICIENT_FUNDS":
                return PaymentFailureReason.INSUFFICIENT_FUNDS;
            case "CARD_EXPIRED":
                return PaymentFailureReason.EXPIRED_CARD;
            case "TIMEOUT":
            case "ISSUER_TIMEOUT":
            case "PAYMENT_REQUEST_EXPIRED":
                return PaymentFailureReason.TIMEOUT;
            case "DECLINED_INVALID_CVV":
            case "DECLINED_CVV":
            case "DECLINED_AVS_CVV":
                return PaymentFailureReason.INCORRECT_CVV;
            default:
                return PaymentFailureReason.PROCESSOR_ERROR;
        }
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
