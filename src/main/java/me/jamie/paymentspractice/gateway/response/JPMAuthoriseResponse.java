package me.jamie.paymentspractice.gateway.response;

public record JPMAuthoriseResponse( String transactionId, String responseStatus, String responseCode, String responseMessage) {
}
