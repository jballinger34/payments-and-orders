package me.jamie.paymentspractice.gateway.request;

public record JPMAuthoriseRequest(
        String captureMethod,
        int amount,
        String currency,
        Merchant merchant,
        PaymentMethodType paymentMethodType
) {

    public record Merchant(
            MerchantSoftware merchantSoftware
    ) {}

    public record MerchantSoftware(
            String companyName,
            String productName
    ) {}

    public record PaymentMethodType(
            Card card
    ) {}

    public record Card(
            String accountNumber,
            Expiry expiry
    ) {}
    public record Expiry(
      int month,
      int year
    ){}
}