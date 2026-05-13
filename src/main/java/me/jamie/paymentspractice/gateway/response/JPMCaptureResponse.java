package me.jamie.paymentspractice.gateway.response;

public record JPMCaptureResponse (String transactionId, String responseStatus, String responseCode, String responseMessage){
}
