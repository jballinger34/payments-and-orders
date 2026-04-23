package domain.model;

public class LineItem {

    private final Product product;
    private final int quantity;
    private final double priceAtPurchase;

    public LineItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = this.product.getCost()*this.quantity;
    }
    public LineItem(Product product, int quantity, double priceAtPurchase){
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }



    public String getProductId(){
        return product.getId();
    }
    public String getProductName(){ return product.getName();}
    public int getQuantity() {
        return quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }
}
