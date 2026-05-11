package com.gigsound.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CancelOrderRequest {

    @NotNull(message = "order_id is required")
    private UUID orderId;

    public CancelOrderRequest() {}

    public CancelOrderRequest(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
}
