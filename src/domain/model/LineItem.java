package domain.model;

public class LineItem {

    private Product product;
    private int quantity;
    private double priceAtPurchase;

    public String getProductId(){
        return product.getId();
    }
    public int getQuantity() {
        return quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }
}
