package me.jamie.paymentspractice.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Merchant {

    @Id
    private String id;
    private String name;
    private String email;

    public Merchant(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    protected Merchant(){};

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
