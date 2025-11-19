package com.example.shop.settlement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"seller_settlement\"", schema = "public")
public class SellerSettlement {

    @Id
    private UUID id;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    protected SellerSettlement() {
    }

    private SellerSettlement(UUID sellerId, UUID orderId, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.sellerId = sellerId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = SettlementStatus.PENDING;
    }

    public static SellerSettlement create(UUID sellerId, UUID orderId, BigDecimal amount) {
        return new SellerSettlement(sellerId, orderId, amount);
    }

    public void markCompleted() {
        this.status = SettlementStatus.COMPLETED;
        this.settledAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = SettlementStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }
}
