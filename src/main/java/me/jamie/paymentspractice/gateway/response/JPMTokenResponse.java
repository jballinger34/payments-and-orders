package me.jamie.paymentspractice.gateway.response;

public record JPMTokenResponse(String access_token, int expires_in, String token_type) {
}
