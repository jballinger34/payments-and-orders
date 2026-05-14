package me.jamie.paymentspractice.gateway;

import me.jamie.paymentspractice.gateway.response.JPMTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
public class JPMAuthService {
    private final RestClient client;

    @Value("${jpm.client-id}")
    private String clientId;
    @Value("${jpm.client-secret}")
    private String clientSecret;

    private String accessToken;
    private Instant expiryTime;

    public JPMAuthService(){
        this.client = RestClient.builder().baseUrl("https://id.payments.jpmorgan.com").build();
    }
    public String getAccessToken() {

        if(accessToken != null && expiryTime != null && Instant.now().isBefore(expiryTime)) {
            return accessToken;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "jpm:payments:sandbox");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        JPMTokenResponse response = client.post().uri("/am/oauth2/alpha/access_token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(JPMTokenResponse.class);

        if(response == null) {
            throw new IllegalStateException("Failed to retrieve OAuth token");
        }

        this.accessToken = response.access_token();
        this.expiryTime = Instant.now().plusSeconds(response.expires_in() - 60);

        return accessToken;

    }

}
