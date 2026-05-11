package com.gigsound.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PurchaseOrderRequest {

    @NotNull(message = "event_id is required")
    private UUID eventId;

    @NotNull(message = "user_id is required")
    private UUID userId;

    @Min(value = 1, message = "quantity must be greater than 0")
    private int quantity;

    public PurchaseOrderRequest() {}

    public PurchaseOrderRequest(UUID eventId, UUID userId, int quantity) {
        this.eventId = eventId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
