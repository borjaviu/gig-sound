package com.gigsound.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventResponse {

    private UUID id;
    private String name;
    private LocalDateTime date;
    private String venue;
    private int totalCapacity;
    private int availableTickets;
    private int ticketPrice;
    private LocalDateTime createdAt;

    public EventResponse() {}

    public EventResponse(UUID id, String name, LocalDateTime date, String venue,
                         int totalCapacity, int availableTickets, int ticketPrice, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.totalCapacity = totalCapacity;
        this.availableTickets = availableTickets;
        this.ticketPrice = ticketPrice;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public int getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(int ticketPrice) { this.ticketPrice = ticketPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
