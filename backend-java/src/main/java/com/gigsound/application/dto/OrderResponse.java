package com.gigsound.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderResponse {

    private UUID id;
    private UUID eventId;
    private UUID userId;
    private int quantity;
    private double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public OrderResponse() {}

    public OrderResponse(UUID id, UUID eventId, UUID userId, int quantity, double totalPrice,
                         String status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
