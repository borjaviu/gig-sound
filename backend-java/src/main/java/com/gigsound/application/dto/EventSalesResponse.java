package com.gigsound.application.dto;

import java.util.List;
import java.util.UUID;

public class EventSalesResponse {

    private UUID eventId;
    private String eventName;
    private int totalSales;
    private double totalRevenue;
    private List<OrderResponse> orders;

    public EventSalesResponse() {}

    public EventSalesResponse(UUID eventId, String eventName, int totalSales,
                               double totalRevenue, List<OrderResponse> orders) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalSales = totalSales;
        this.totalRevenue = totalRevenue;
        this.orders = orders;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public List<OrderResponse> getOrders() { return orders; }
    public void setOrders(List<OrderResponse> orders) { this.orders = orders; }
}
