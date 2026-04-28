package me.jamie.paymentspractice.gateway;

public interface PaymentGateway extends PSPClient, PSPListener{

    //marker interface - temporary - should instead have a facade pattern
    // i.e. PaymentPSP that has a client and listener
}
