package domain.network;

public interface PaymentNetwork {
    void capture();
    void clear();
    void settle();
}
