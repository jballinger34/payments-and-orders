package domain.model;

public class Product {

    private final String id;
    private String name;
    private double cost;
    public Product(String id, String name, double cost){
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }
}
