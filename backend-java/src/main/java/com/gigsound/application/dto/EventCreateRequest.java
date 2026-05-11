package com.gigsound.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventCreateRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "date is required")
    private LocalDateTime date;

    @NotBlank(message = "venue is required")
    private String venue;

    @Min(value = 1, message = "total_capacity must be greater than 0")
    private int totalCapacity;

    @Min(value = 1, message = "ticket_price must be greater than 0")
    private int ticketPrice = 50;

    public EventCreateRequest() {}

    public EventCreateRequest(String name, LocalDateTime date, String venue, int totalCapacity, int ticketPrice) {
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.totalCapacity = totalCapacity;
        this.ticketPrice = ticketPrice;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(int ticketPrice) { this.ticketPrice = ticketPrice; }
}
