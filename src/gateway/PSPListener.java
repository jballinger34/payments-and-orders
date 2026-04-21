package gateway;

/**
 * Listens to webhooks, etc. from external PSP
 * PSP says "I have cleared this payment" or "I have settled this payment"
 */
public interface PSPListener {

    void onCleared();
    void onSettled();
}
