package me.jamie.paymentspractice.gateway;

import me.jamie.paymentspractice.gateway.request.JPMAuthoriseRequest;
import me.jamie.paymentspractice.gateway.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.domain.model.payment.Payment;
import me.jamie.paymentspractice.domain.model.payment.PaymentFailureReason;
import me.jamie.paymentspractice.domain.model.payment.PaymentResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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
        JPMAuthoriseRequest request = new JPMAuthoriseRequest(
                "MANUAL",
                (int)(payment.getAmount()*100),
                "USD",
                new JPMAuthoriseRequest.Merchant(
                        new JPMAuthoriseRequest.MerchantSoftware(
                                "Jamie Test Payments Ltd",
                                "Payments Practice"
                        )
                ),
                new JPMAuthoriseRequest.PaymentMethodType(
                        new JPMAuthoriseRequest.Card(
                                //TEST FOR NOW
                                "4012000033330026",
                                new JPMAuthoriseRequest.Expiry(
                                        5,
                                        2025
                                )
                        )
                )
        );
        try{
            JPMAuthoriseResponse response = client.post().uri("/payments")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                    .header("merchant-id", "998482157630")
                    .header("request-id", payment.getId())
                    .body(request)
                    .retrieve()
                    .body(JPMAuthoriseResponse.class);
            if(response == null){
                return new PaymentResponse(false, PaymentFailureReason.PROCESSOR_ERROR,null);
            } else if("SUCCESS".equals(response.responseStatus())){
                return new PaymentResponse(true,null, response.transactionId());
            } else {
                // TODO MAP THE JPM RESPONSE INTO A PaymentFailureReason
                return new PaymentResponse(false, PaymentFailureReason.CARD_BLOCKED, response.transactionId());
            }

        } catch (Exception e){
            e.printStackTrace();
            return new PaymentResponse(false, PaymentFailureReason.PROCESSOR_ERROR, null);
        }

    }

    @Override
    public PaymentResponse capture(Payment payment) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void onCleared() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void onSettled() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
