package me.jamie.paymentspractice.gateway.jpm.response;

public record JPMCaptureResponse (String transactionId, String responseStatus, String responseCode, String responseMessage){
}
