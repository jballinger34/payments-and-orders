package me.jamie.paymentspractice.data.entity;

import jakarta.persistence.*;
import me.jamie.paymentspractice.domain.model.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_orders_merchant_status", columnList = "merchant_id, status")
        }
)
public class OrderEntity {

    @Id
    private String id;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<LineItemEntity> items = new ArrayList<>();

    public OrderEntity() {}

    public OrderEntity(String id, String merchantId, OrderStatus status, String paymentId) {
        this.id = id;
        this.merchantId = merchantId;
        this.status = status;
        this.paymentId = paymentId;
    }

    public void setItems(List<LineItemEntity> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public List<LineItemEntity> getItems() {
        return items;
    }
}