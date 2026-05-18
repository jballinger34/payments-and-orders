package me.jamie.paymentspractice.gateway.jpm;

import me.jamie.paymentspractice.gateway.jpm.request.JPMRequest;
import me.jamie.paymentspractice.gateway.jpm.response.JPMAuthoriseResponse;
import me.jamie.paymentspractice.gateway.jpm.response.JPMCaptureResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jpm.merchant-id}")
    private String merchantId;

    public JPMHttpClient(JPMAuthService authService, @Value("${jpm.base-url}") String url) {

        this.authService = authService;

        this.client = RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public JPMAuthoriseResponse sendAuthorizeRequest(JPMRequest request) {
        return client.post()
                .uri("/payments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .header("merchant-id", merchantId)
                .header("request-id", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .body(JPMAuthoriseResponse.class);
    }

    public JPMCaptureResponse sendCaptureRequest(String providerReference, JPMRequest request) {
        return client.post()
                .uri("/payments/" + providerReference + "/captures")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .header("merchant-id", merchantId)
                .header("request-id", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .body(JPMCaptureResponse.class);
    }


}
