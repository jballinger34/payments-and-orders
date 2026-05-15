package me.jamie.paymentspractice.gateway.jpm;

import me.jamie.paymentspractice.gateway.jpm.request.JPMRequest;
import me.jamie.paymentspractice.gateway.jpm.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.gateway.jpm.response.JPMCaptureResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class JPMHttpClient {

    private final RestClient client;
    //where we get a JPM API key from to auth/capture payments with them
    private final JPMAuthService authService;

    public JPMHttpClient(JPMAuthService authService) {

        this.authService = authService;

        this.client = RestClient.builder()
                .baseUrl("https://api-mock.payments.jpmorgan.com/api/v2")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public JPMAuthoriseResponse sendAuthorizeRequest(JPMRequest request) {
        return client.post()
                .uri("/payments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .header("merchant-id", "998482157630")
                .header("request-id", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .body(JPMAuthoriseResponse.class);
    }

    public JPMCaptureResponse sendCaptureRequest(String providerReference, JPMRequest request) {
        return client.post()
                .uri("/payments/" + providerReference + "/captures")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .header("merchant-id", "998482157630")
                .header("request-id", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .body(JPMCaptureResponse.class);
    }


}
