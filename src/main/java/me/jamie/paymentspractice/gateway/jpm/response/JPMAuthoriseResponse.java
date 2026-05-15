package me.jamie.paymentspractice.gateway.jpm.response;

public record JPMAuthoriseResponse( String transactionId, String responseStatus, String responseCode, String responseMessage) {
}
